package com.verdy.personalassistant.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.verdy.personalassistant.fragment.OnAdapterItemSwipedListener;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private OnAdapterItemSwipedListener swipeListener;

    public SwipeToDeleteCallback(OnAdapterItemSwipedListener swipeListener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        swipeListener.onAdapterItemSwiped(position);
    }

}
