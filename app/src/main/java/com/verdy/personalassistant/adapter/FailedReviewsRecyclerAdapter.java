package com.verdy.personalassistant.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.fragment.OnAdapterItemSwipedListener;
import com.verdy.personalassistant.fragment.ReviewsReminderFragment;
import com.verdy.personalassistant.model.ReviewReminder;

import java.util.ArrayList;

public class FailedReviewsRecyclerAdapter extends RecyclerView.Adapter<FailedReviewsRecyclerAdapter.ViewHolder> implements OnAdapterItemSwipedListener {
    private ArrayList<ReviewReminder> failedReviews;
    private ReviewsReminderFragment context;

    public FailedReviewsRecyclerAdapter(ArrayList<ReviewReminder> failedReviews, ReviewsReminderFragment context){
        this.failedReviews = failedReviews;
        this.context = context;

    }

    @NonNull
    @Override
    public FailedReviewsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.failed_reviews_items, parent, false);
        ((TextView)view.getChildAt(0)).setSpannableFactory(spannableFactory);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FailedReviewsRecyclerAdapter.ViewHolder holder, int position) {
        ReviewReminder failedReview = failedReviews.get(position);
        int nameLength = failedReview.subjectName.length() + failedReview.topicName.length() +  + 2;
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(failedReview.subjectName);
        sb.append(": ");
        sb.append(failedReview.topicName);
        sb.setSpan(new ForegroundColorSpan(Color.BLACK), 0, nameLength , 0);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, nameLength, 0);
        sb.append("\n").append(failedReview.topicNote);
        holder.reviewDescription.setText(sb, TextView.BufferType.SPANNABLE);
        holder.reviewDescription.setTag(position);
        holder.reviewDate.setText(failedReview.date);
    }

    @Override
    public int getItemCount() {
        return failedReviews.size();
    }

    private Spannable.Factory spannableFactory = new Spannable.Factory(){
        @Override
        public Spannable newSpannable(CharSequence source) {
            return (Spannable) source;
        }
    };

    @Override
    public void onAdapterItemSwiped(int itemPosition) {
        context.updateReviewState(failedReviews.get(itemPosition).reviewId);
        failedReviews.remove(itemPosition);
        notifyItemRemoved(itemPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView reviewDescription;
        TextView reviewDate;
        ViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            reviewDescription = (TextView) itemView.getChildAt(0);
            reviewDate = (TextView) itemView.getChildAt(1);
        }
    }
}
