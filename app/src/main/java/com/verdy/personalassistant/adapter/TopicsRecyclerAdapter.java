package com.verdy.personalassistant.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.fragment.OnAdapterItemSwipedListener;
import com.verdy.personalassistant.fragment.OnFragmentCallBack;
import com.verdy.personalassistant.fragment.TopicEditFragment;
import com.verdy.personalassistant.model.Topic;

import java.util.ArrayList;

public class TopicsRecyclerAdapter extends RecyclerView.Adapter<TopicsRecyclerAdapter.ViewHolder> implements OnAdapterItemSwipedListener, View.OnClickListener {
    private ArrayList<Topic> topics;
    private OnFragmentCallBack fragmentCallBack;
    private int itemToEditPosition;
    private boolean isReviewsFragment;

    public TopicsRecyclerAdapter(ArrayList<Topic> topics, OnFragmentCallBack callBack, boolean isReviewsFragment ){
        this.topics = topics;
        this.fragmentCallBack =callBack;
        this.isReviewsFragment = isReviewsFragment;
    }

    @NonNull
    @Override
    public TopicsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_date_view, parent, false);
        textView.setSpannableFactory(spannableFactory);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicsRecyclerAdapter.ViewHolder holder, int position) {
        Topic topic = topics.get(position);
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        int titleLength = isReviewsFragment? topic.subjectName.length() + topic.name.length() + 2 : topic.name.length();
        if(isReviewsFragment){
            sb.append(topic.subjectName);
            sb.append(": ");
            sb.append(topic.name);

        }else{
            sb.append(topic.name);
            holder.topicDescription.setOnClickListener(this);
        }
        sb.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleLength , 0);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleLength, 0);
        sb.append("\n").append(topic.notes);
        holder.topicDescription.setText(sb, TextView.BufferType.SPANNABLE);
        holder.topicDescription.setTag(position);
    }

    @Override
    public int getItemCount() {
        return topics != null ? topics.size() : 0;
    }

    public void loadNewTopic(final Topic topic){
        topics.add(topic);
        notifyDataSetChanged();
    }

    private Spannable.Factory spannableFactory = new Spannable.Factory(){
        @Override
        public Spannable newSpannable(CharSequence source) {
            return (Spannable) source;
        }
    };

    @Override
    public void onAdapterItemSwiped(int itemPosition) {
        fragmentCallBack.itemSwiped(topics.get(itemPosition));
        topics.remove(itemPosition);
        notifyItemRemoved(itemPosition);
    }

    @Override
    public void onClick(View v) {
        itemToEditPosition = (int)v.getTag();
        TopicEditFragment fragment = TopicEditFragment.newInstance();
        fragment.setTopicToEdit(topics.get(itemToEditPosition), ((Fragment) fragmentCallBack).getContext());
        fragment.setTopicsAdapterRef(this);
        fragment.show(((Fragment) fragmentCallBack).getActivity().getSupportFragmentManager(), TopicEditFragment.TAG);
    }

    public void updateEditedItem(){
        notifyItemChanged(itemToEditPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView topicDescription;
        ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            topicDescription = itemView;
        }
    }
}
