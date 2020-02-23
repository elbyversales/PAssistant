package com.verdy.personalassistant.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.adapter.TopicsRecyclerAdapter;
import com.verdy.personalassistant.controller.MainActivity;
import com.verdy.personalassistant.dao.DAO;
import com.verdy.personalassistant.dao.DAOManager;
import com.verdy.personalassistant.dao.TopicDao;
import com.verdy.personalassistant.databinding.RecyclerAndEmptyTagBinding;
import com.verdy.personalassistant.model.Subject;
import com.verdy.personalassistant.model.Topic;
import com.verdy.personalassistant.util.SwipeToDeleteCallback;

import java.util.ArrayList;

public class TopicsFragment extends Fragment implements OnFragmentCallBack, DialogInterface.OnClickListener {
    public static final String TAG = "topics_fragent";
    private final String SUBJECT_ID = "_id";
    private final String SUBJECT_NAME = "name";
    private final ObservableBoolean isListEmpty = new ObservableBoolean();
    private MainActivity activity;
    private RecyclerView topicsRecycler;
    private TopicsRecyclerAdapter recyclerAdapter;
    private Subject subject;
    private ArrayList<Topic> topics;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        restoreSubject();
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RecyclerAndEmptyTagBinding binding = DataBindingUtil.inflate(inflater, R.layout.recycler_and_empty_tag, container, false);
        topicsRecycler = binding.topicsRecycler;
        topics = TopicDao.getTopics(subject._id);
        isListEmpty.set(topics.size() == 0);
        activity.setTitle(subject.name);
        binding.setIsListEmpty(isListEmpty);
        initTopicsRecycler();
        return binding.getRoot();
    }

    private void restoreSubject(){
        if(subject == null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            String name = prefs.getString(SUBJECT_NAME, "");
            int _id = prefs.getInt(SUBJECT_ID, 0);
            subject = new Subject(_id, name);
        }
    }

    private void initTopicsRecycler(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        topicsRecycler.setLayoutManager(mLayoutManager);
        topicsRecycler.setHasFixedSize(true);
        recyclerAdapter = new TopicsRecyclerAdapter(topics, this, false);
        topicsRecycler.setAdapter(recyclerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(recyclerAdapter));
        itemTouchHelper.attachToRecyclerView(topicsRecycler);
    }

    @Override
    public void itemSwiped(DAO item) {
        String msg = DAOManager.delete(item)? "Topic deleted" : "Cannot delete the topic, an error occurred";
        Toast.makeText(activity, msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.topics_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if(itemId == R.id.add_topic){
            NewTopicEntryFragment fragment = new NewTopicEntryFragment();
            fragment.setParentFragment(this);
            fragment.setSubjectId(subject._id, this.getContext());
            fragment.show(activity.getSupportFragmentManager(), NewTopicEntryFragment.TAG);
            return true;
        }else if(itemId == R.id.delete_subject){
            showDeleteDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void loadNewTopic(final Topic topic){
        recyclerAdapter.loadNewTopic(topic);
        isListEmpty.set(topics.size() == 0);
    }

    public void resetTopics(final Subject subject){
        this.subject = subject;
        activity.setTitle(subject.name);
        topics = TopicDao.getTopics(subject._id);
        recyclerAdapter.notifyDataSetChanged();
        isListEmpty.set(topics.size() == 0);
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("DELETE", this );
        builder.setTitle("Delete Subject").setMessage("Do you want to delete this subject?")
                .setNegativeButton("CANCEL", null)
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        DAOManager.delete(subject);
        activity.deleteSubjectOnAdapter(subject);
    }

    public void setSubject(Subject subject, Context context) {
        this.subject = subject;
        Log.d("TopicsFragment", "Setting subject = " + subject);
        Log.d("Context", "With context = " + context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SUBJECT_ID, subject._id);
        editor.putString(SUBJECT_NAME, subject.name);
        editor.commit();
    }
}
