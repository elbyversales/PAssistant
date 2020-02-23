package com.verdy.personalassistant.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.adapter.FailedReviewsRecyclerAdapter;
import com.verdy.personalassistant.adapter.TopicsRecyclerAdapter;
import com.verdy.personalassistant.controller.MainActivity;
import com.verdy.personalassistant.dao.DAOManager;
import com.verdy.personalassistant.dao.DAO;
import com.verdy.personalassistant.model.ReviewReminder;
import com.verdy.personalassistant.model.Topic;
import com.verdy.personalassistant.util.SwipeToDeleteCallback;

import java.util.ArrayList;

/*      no_reviews_tag 0
        failed_reviews_tag 1,
        failed_reviews_recycler 2,
        today_reviews_tag 3,
        reviews_recycler 4*/

public class ReviewsReminderFragment extends Fragment implements OnFragmentCallBack {
    public static final String TAG = "reviews_reminder_fragent";
    private MainActivity activity;
    private String currentDate;
    private LinearLayout view;
    private ArrayList<Topic> reminders;
    private ArrayList<ReviewReminder> failedReminders;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = (LinearLayout) inflater.inflate(R.layout.review_reminders, container, false);
        activity.setTitle("Reviews");
        setupFailedReminders();
        setupTodayReviews();
        isNoReviews();
        return view;
    }

    private void setupFailedReminders(){
        if(failedReminders != null){
            RecyclerView failedReviewsRecycler = (RecyclerView) view.getChildAt(2);
            RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            failedReviewsRecycler.setLayoutManager(linearLayoutManager);
            failedReviewsRecycler.setHasFixedSize(true);
            FailedReviewsRecyclerAdapter failedReviewsRecyclerAdapter = new FailedReviewsRecyclerAdapter(failedReminders, this);
            failedReviewsRecycler.setAdapter(failedReviewsRecyclerAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(failedReviewsRecyclerAdapter));
            touchHelper.attachToRecyclerView(failedReviewsRecycler);
        }else{
            view.getChildAt(1).setVisibility(View.GONE);
            view.getChildAt(2).setVisibility(View.GONE);
        }
    }

    private void setupTodayReviews(){
        if(reminders != null){
            ((TextView) view.getChildAt(3)).setText("Reviews for today   " + currentDate);
            RecyclerView reviewsRecycler = (RecyclerView) view.getChildAt(4);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
            reviewsRecycler.setLayoutManager(layoutManager);
            reviewsRecycler.setHasFixedSize(true);
            TopicsRecyclerAdapter reviewsRecyclerAdapter = new TopicsRecyclerAdapter(reminders, this, true);
            reviewsRecycler.setAdapter(reviewsRecyclerAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(reviewsRecyclerAdapter));
            itemTouchHelper.attachToRecyclerView(reviewsRecycler);
        }else{
            view.getChildAt(3).setVisibility(View.GONE);
            view.getChildAt(4).setVisibility(View.GONE);
        }
    }

    private void isNoReviews(){
        if(reminders == null && failedReminders == null){
            view.getChildAt(0).setVisibility(View.VISIBLE);
        }
    }

    public void setInitialValues(final ArrayList<ReviewReminder> failedReminders, final ArrayList<Topic> topics, final String currentDate){
        this.reminders = topics;
        this.failedReminders = failedReminders;
        this.currentDate = currentDate;
    }

    @Override
    public void itemSwiped(DAO item) {
        updateReviewState(((Topic)item).auxReviewId);
    }

    public void updateReviewState(final int _id){
        DAOManager.execute("UPDATE review SET checked = 1 WHERE _id = " + _id);
    }

}