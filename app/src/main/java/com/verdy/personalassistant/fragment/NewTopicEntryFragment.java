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
import com.verdy.personalassistant.adapter.ReviewDatesRecyclerAdapter;
import com.verdy.personalassistant.dao.TopicInsertDao;
import com.verdy.personalassistant.model.Topic;
import com.verdy.personalassistant.util.FormatterUtility;
import com.verdy.personalassistant.util.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.Calendar;

/*new_topic_title 0,
        save_topic 1,
        topic_name 2,
        topic_note 3,
        hint_reviews_tag 4,
        add_review 5
        review_dates 6*/

public class NewTopicEntryFragment extends DialogFragment implements OnDateSelectedListener, View.OnClickListener {
    static String TAG = "new_topic_entry_fragment";
    private final String NEWT_SUBJECT_ID = "newtSubjectId";
    private TopicsFragment parentFragment;
    private EditText name;
    private EditText notes;
    private RecyclerView reviewDatesRecycler;
    private ReviewDatesRecyclerAdapter recyclerAdapter;
    private ArrayList<String> dates = new ArrayList<>(4);
    private Calendar calendar = Calendar.getInstance();
    private int subjectId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        restoreSubjectId();
    }

    private void restoreSubjectId(){
        if(subjectId == 0){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            subjectId = prefs.getInt(NEWT_SUBJECT_ID, 0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.new_topic_entry, container, false);
        setViews(view);
        generateDates();
        initReviewsDatesRecycler();
        return view;
    }

    private void setViews(final ConstraintLayout view){
        view.getChildAt(1).setOnClickListener(this);
        view.getChildAt(5).setOnClickListener(this);
        name = (EditText) view.getChildAt(2);
        notes = (EditText) view.getChildAt(3);
        reviewDatesRecycler = (RecyclerView) view.getChildAt(6);
    }

    private void generateDates(){
        setInitDate();
        dates.add(FormatterUtility.formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        for(int index = 1, dayCounter = 1; index < 4; index ++){
            dayCounter *= 2;
            calendar.add(Calendar.DAY_OF_MONTH, dayCounter);
            dates.add(FormatterUtility.formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        }
    }

    private void initReviewsDatesRecycler(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        reviewDatesRecycler.setLayoutManager(mLayoutManager);
        reviewDatesRecycler.setHasFixedSize(true);
        recyclerAdapter = new ReviewDatesRecyclerAdapter(dates, this);
        reviewDatesRecycler.setAdapter(recyclerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(recyclerAdapter));
        itemTouchHelper.attachToRecyclerView(reviewDatesRecycler);
    }

    private void setInitDate(){
        if(calendar.get(Calendar.HOUR_OF_DAY) > 12){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void showDatePicker(){
        recyclerAdapter.resetSelectedPosition();
        DateDialog dateDialog = new DateDialog();
        dateDialog.setSelectedDateListener(this);
        dateDialog.show(this.getActivity().getSupportFragmentManager(), "a_date");
    }

    private void insertNewTopic(){
        String msg;
        if(validTopic()){
            Topic topic = new Topic();
            setTopicValues(topic);
            TopicInsertDao.save(topic);
            if(parentFragment != null){
                parentFragment.loadNewTopic(topic);
            }
            msg = "Topic saved";
            this.dismiss();
        }else{
            msg = "The topic name cannot be empty";
        }
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setTopicValues(Topic topic){
        topic.name = name.getText().toString();
        topic.notes = notes.getText().toString();
        topic.reviewDates = dates.isEmpty()? null : dates;
        topic.subjectId = subjectId;
    }

    private boolean validTopic(){
        return !name.getText().toString().isEmpty();
    }

    @Override
    public void onDateSelected(String value) {
        if(recyclerAdapter.getSelectedPosition() >= 0){
            int position = recyclerAdapter.getSelectedPosition();
            dates.set(position, value);
            recyclerAdapter.notifyItemChanged(position);
        }else{
            dates.add(value);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_topic:
                insertNewTopic();
                break;
            case R.id.add_review:
                showDatePicker();
        }
    }

    void setParentFragment(TopicsFragment parentFragment){
        this.parentFragment = parentFragment;
    }

    void setSubjectId(int subjectId, Context context){
        this.subjectId = subjectId;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(NEWT_SUBJECT_ID, subjectId);
        editor.apply();

    }

}
