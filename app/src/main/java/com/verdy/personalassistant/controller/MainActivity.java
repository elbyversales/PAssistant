package com.verdy.personalassistant.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.adapter.NavDrawerExpandableAdapter;
import com.verdy.personalassistant.dao.ReviewDao;
import com.verdy.personalassistant.dao.SubjectDao;
import com.verdy.personalassistant.fragment.AddSubjectFragment;
import com.verdy.personalassistant.fragment.ReviewsReminderFragment;
import com.verdy.personalassistant.fragment.TopicsFragment;
import com.verdy.personalassistant.model.ReviewReminder;
import com.verdy.personalassistant.model.Subject;
import com.verdy.personalassistant.model.Topic;
import com.verdy.personalassistant.util.Constant;
import com.verdy.personalassistant.util.FormatterUtility;
import com.verdy.personalassistant.util.MApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener, View.OnClickListener{
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private boolean drawerItemSelected;
    private NavDrawerExpandableAdapter expandableListAdapter;
    private ExpandableListView expandableListView;
    private int currentSubjectId;
    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "on activity create");
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            String[] perms =  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, perms, Constant.REQUEST_WRITE_EXTERNAL_STORAGE_PERM);
        }else{
            initViews();
        }
    }

    private void initViews(){
        Log.d("MainActivity", "on initViews");
        setContentView(R.layout.content_main_c);
        initToolBar();
        initDrawer();
        setupReviewsFragment();
    }

    public void setupReviewsFragment(){
        Calendar calendar = Calendar.getInstance();
        String date = FormatterUtility.formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        ArrayList<Topic> topicsToReview = (ArrayList<Topic>) ReviewDao.getReviews(ReviewDao.ConditionFlags.TOPICS_TO_REMINDER_BY_DAY, 0, date);
        ArrayList<ReviewReminder> failedReviews = (ArrayList<ReviewReminder>) ReviewDao.getReviews(ReviewDao.ConditionFlags.FAILED_REVIEWS, 0, date);
        ReviewsReminderFragment reviewsReminderFragment = new ReviewsReminderFragment();
        reviewsReminderFragment.setInitialValues(failedReviews, topicsToReview, date);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, reviewsReminderFragment, ReviewsReminderFragment.TAG).commit();
        currentFragment = Constant.NAV_REVIEWS;
        currentSubjectId = -1;
    }

    private void initToolBar(){
        toolbar =  findViewById(R.id.main_nav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawer(){
        drawerLayout = this.findViewById(R.id.navigation_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,  R.string.drawer_open, R.string.drawer_close ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(drawerItemSelected){
                    setupReviewsFragment();
                    drawerItemSelected = false;
                }
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        initDrawerNavList();
    }

    private void initDrawerNavList(){
        LinearLayout linearLayout = (LinearLayout)drawerLayout.getChildAt(1);
        ((LinearLayout)linearLayout.getChildAt(0)).getChildAt(0).setOnClickListener(this);
        expandableListView = (ExpandableListView) linearLayout.getChildAt(1);
        expandableListAdapter = new NavDrawerExpandableAdapter(this, SubjectDao.getSubjects());
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupClickListener(this);
        expandableListView.setOnChildClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constant.REQUEST_WRITE_EXTERNAL_STORAGE_PERM){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                this.initViews();
            }else{
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.config_alarm){
            startActivity(new Intent( this, ReminderReviewConfigFragment.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void extractDB(){
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/PA.db";
                String backupDBPath = "/PAssistant/db/backuppa.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    MediaScannerConnection.scanFile(MApp.mAppContext, new String[] {backupDB.getPath()}, null, null);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null)  mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        final Subject subject = expandableListAdapter.getChild(1, childPosition );
        if(subject._id != currentSubjectId){
            currentSubjectId = subject._id;
            if(currentFragment != Constant.TOPICS_FRAGMENT){
                TopicsFragment fragment = new TopicsFragment();
                fragment.setSubject(subject, this);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment, TopicsFragment.TAG).commit();
                currentFragment = Constant.TOPICS_FRAGMENT;
            }else{
                TopicsFragment fragment = (TopicsFragment) getSupportFragmentManager().findFragmentByTag(TopicsFragment.TAG);
                fragment.resetTopics(subject);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        int selectedItem = expandableListAdapter.getGroup(groupPosition).navId;
        if(currentFragment != Constant.NAV_REVIEWS && selectedItem == Constant.NAV_REVIEWS){
            drawerItemSelected = true;
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    public void addSubject(final Subject subject){
        expandableListAdapter.addNewSubject(subject);
    }

    @Override
    public void onClick(View v) {
        AddSubjectFragment fragment = AddSubjectFragment.newInstance();
        fragment.show(getSupportFragmentManager(), AddSubjectFragment.TAG);
    }

    public void deleteSubjectOnAdapter(final Subject subject){
        expandableListAdapter.deleteSubject(subject);
        expandableListView.invalidateViews();
        currentSubjectId = -1;
        setupReviewsFragment();
    }

}


