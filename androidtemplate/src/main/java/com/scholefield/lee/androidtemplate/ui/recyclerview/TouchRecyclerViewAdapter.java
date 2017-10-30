package com.scholefield.lee.androidtemplate.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

/**
 * Extension of {@link BaseRecyclerViewAdapter}.
 *
 * This adds an onTouch callback to the attached RecyclerView. By default dragging an item will change its position within
 * the recycler view, and a left or right swipe will do nothing. To implement custom behaviour you must override {@link #onSwipeLeft},
 * {@link #onSwipeRight} and {@link #onItemMove}.
 *
 * @param <T> the type of object this recyclerView displays.
 * @param <VH> ViewHolder subclass.
 * @see BaseRecyclerViewAdapter
 */
public abstract class TouchRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends BaseRecyclerViewAdapter<T, VH>
        implements ItemTouchHelperAdapter {

    private SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(this);
    private ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

    protected RecyclerView recyclerView;

    public TouchRecyclerViewAdapter(List<T> items) {
        super(items);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;

        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSwipeLeft(int position) {

    }

    @Override
    public void onSwipeRight(int position) {

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(dataSet, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
}
