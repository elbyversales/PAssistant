package com.verdy.personalassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.fragment.OnAdapterItemSwipedListener;
import com.verdy.personalassistant.fragment.TopicEditFragment;
import com.verdy.personalassistant.model.Review;

import java.util.ArrayList;

public class ReviewTopicEditRecyclerAdapter extends RecyclerView.Adapter<ReviewTopicEditRecyclerAdapter.ViewHolder> implements OnAdapterItemSwipedListener {
    private ArrayList<Review> reviews;
    private TopicEditFragment context;

    public ReviewTopicEditRecyclerAdapter(ArrayList<Review> reviews, TopicEditFragment context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewTopicEditRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder((LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_date_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewTopicEditRecyclerAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.date.setText(review.date);
        holder.checked.setVisibility(review.checked? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @Override
    public void onAdapterItemSwiped(int itemPosition) {
        context.deleteReview(reviews.get(itemPosition));
        reviews.remove(itemPosition);
        notifyItemRemoved(itemPosition);
    }

    public void addReview(final Review review){
        reviews.add(review);
        notifyItemInserted(reviews.size() - 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        ImageView checked;

        ViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            date = (TextView) itemView.getChildAt(0);
            checked = (ImageView) itemView.getChildAt(1);
        }
    }
}
