package com.verdy.personalassistant.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.adapter.ReviewTopicEditRecyclerAdapter;
import com.verdy.personalassistant.adapter.TopicsRecyclerAdapter;
import com.verdy.personalassistant.dao.DAOManager;
import com.verdy.personalassistant.dao.ReviewDao;
import com.verdy.personalassistant.dao.TopicInsertDao;
import com.verdy.personalassistant.model.Review;
import com.verdy.personalassistant.model.Topic;
import com.verdy.personalassistant.util.FormatterUtility;
import com.verdy.personalassistant.util.SwipeToDeleteCallback;

import java.util.ArrayList;

/*topic_title_tag 0,
save_topic 1,
topic_name_edit 2,
topic_note_edit 3,
hint_reviews_tag 4,
add_review 5,
review_dates 6*/

public class TopicEditFragment extends DialogFragment implements OnDateSelectedListener, View.OnClickListener {
    public static String TAG = "topic_edit_fragment";
    private Topic topic;
    private TopicsRecyclerAdapter topicsAdapterRef;
    private RecyclerView reviewDatesRecycler;
    private ReviewTopicEditRecyclerAdapter recyclerAdapter;
    private ConstraintLayout view;
    private final String TOPIC_ID = "topic_id";
    private final String TOPIC_NAME = "topic_name";
    private final String TOPIC_NOTES = "topic_notes";

    public static TopicEditFragment newInstance(){
        return new TopicEditFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        restoreTopic();
    }

    private void restoreTopic(){
        if(topic == null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            int _id = prefs.getInt(TOPIC_ID, 0);
            String name = prefs.getString(TOPIC_NAME, "");
            String notes = prefs.getString(TOPIC_NOTES, "");
            topic = new Topic(_id, name, notes);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = (ConstraintLayout) inflater.inflate(R.layout.topic_edit, container, false);
        setupViews();
        initReviewsRecycler();
        return view;
    }

    private void setupViews(){
        view.getChildAt(1).setOnClickListener(this);
        view.getChildAt(5).setOnClickListener(this);
        reviewDatesRecycler = (RecyclerView) view.getChildAt(6);
        ((EditText)view.getChildAt(2)).setText(topic.name);
        ((EditText)view.getChildAt(3)).setText(topic.notes);
    }

    private void initReviewsRecycler(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        reviewDatesRecycler.setLayoutManager(mLayoutManager);
        reviewDatesRecycler.setHasFixedSize(true);
        initAdapter((ArrayList<Review>) ReviewDao.getReviews(ReviewDao.ConditionFlags.REVIEWS_BY_TOPIC, topic._id,null));
    }

    private void initAdapter(ArrayList<Review> reviews){
        if(reviews != null){
            recyclerAdapter = new ReviewTopicEditRecyclerAdapter(reviews, this);
            reviewDatesRecycler.setAdapter(recyclerAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(recyclerAdapter));
            itemTouchHelper.attachToRecyclerView(reviewDatesRecycler);
        }
    }

    private void showDatePicker(){
        DateDialog dateDialog = new DateDialog();
        dateDialog.setSelectedDateListener(this);
        dateDialog.show(getActivity().getSupportFragmentManager(), "a_date");
    }

    private void updateTopic(){
        if(validateTopic()){
            String msg = DAOManager.update(topic)? "Topic updated" : "Error, topic not upated";
            Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
            if(topicsAdapterRef != null){
                topicsAdapterRef.updateEditedItem();
            }
            this.dismiss();
        }else{
            Toast.makeText(this.getActivity(), "topic data not valid", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateTopic(){
        String name = ((EditText)view.getChildAt(2)).getText().toString();
        String notes = ((EditText)view.getChildAt(3)).getText().toString();
        if((!name.isEmpty() && !name.equals(topic.name)) || (!notes.isEmpty() && !notes.equals(topic.notes))){
            topic.name = name;
            topic.notes = notes;
            return true;
        }
        return false;
    }

    public void deleteReview(final Review review){
        String msg = DAOManager.delete(review)? "Review deleted" : "Cannot delete the review, an error occurred";
        Toast.makeText(this.getActivity(), msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSelected(String reviewDate) {
        Review review = new Review(topic._id, reviewDate);
        if(recyclerAdapter == null){
            ArrayList<Review> reviews = new ArrayList<>(1);
            reviews.add(review);
            initAdapter(reviews);
        }else{
            recyclerAdapter.addReview(review);
        }
        int reviewId = DAOManager.save(review);
        TopicInsertDao.processReviewForToday(FormatterUtility.getTodaysDate(), reviewDate,reviewId, topic._id);
        String msg = reviewId > 0? "review saved" : "review was not saved";
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_review:
                showDatePicker();
                break;
            case R.id.save_topic:
                updateTopic();
        }
    }

    public void setTopicToEdit(final Topic topic, Context context){
        this.topic = topic;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(TOPIC_ID, topic._id);
        editor.putString(TOPIC_NAME, topic.name);
        editor.putString(TOPIC_NOTES, topic.notes);
        editor.commit();
    }

    public void setTopicsAdapterRef(final TopicsRecyclerAdapter adapterRef){
        this.topicsAdapterRef = adapterRef;
    }
}
