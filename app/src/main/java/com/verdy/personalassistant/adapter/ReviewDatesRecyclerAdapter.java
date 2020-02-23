package com.verdy.personalassistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.fragment.DateDialog;
import com.verdy.personalassistant.fragment.NewTopicEntryFragment;
import com.verdy.personalassistant.fragment.OnAdapterItemSwipedListener;

import java.util.ArrayList;

public class ReviewDatesRecyclerAdapter extends RecyclerView.Adapter<ReviewDatesRecyclerAdapter.ViewHolder> implements OnAdapterItemSwipedListener, View.OnClickListener {
    private ArrayList<String> reviewDates;
    private NewTopicEntryFragment context;
    private int selectedPosition = -1;

    public ReviewDatesRecyclerAdapter(ArrayList<String> reviewDates, NewTopicEntryFragment context){
        this.reviewDates = reviewDates;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewDatesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder((TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_date_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewDatesRecyclerAdapter.ViewHolder holder, int position) {
        holder.reviewDate.setText(reviewDates.get(position));
        holder.reviewDate.setTag(position);
        holder.reviewDate.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return reviewDates.size();
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    public void resetSelectedPosition(){
        selectedPosition = -1;
    }

    @Override
    public void onAdapterItemSwiped(int itemPosition) {
        reviewDates.remove(itemPosition);
        notifyItemRemoved(itemPosition);
    }

    @Override
    public void onClick(View v) {
        selectedPosition = (int) v.getTag();
        DateDialog dateDialog = new DateDialog();
        dateDialog.setSelectedDateListener(context);
        dateDialog.show(context.getActivity().getSupportFragmentManager(), "a_date");
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView reviewDate;
        ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            reviewDate = itemView;
        }
    }
}
