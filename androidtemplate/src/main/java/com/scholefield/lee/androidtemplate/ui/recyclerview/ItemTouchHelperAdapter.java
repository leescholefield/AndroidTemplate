package com.scholefield.lee.androidtemplate.ui.recyclerview;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

/**
 *
 */
public interface ItemTouchHelperAdapter {

    void animateLeftSwipe(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY);

    void onSwipeLeft(int position);

    void animateRightSwipe(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY);

    void onSwipeRight(int position);

    void onItemMove(int fromPosition, int toPosition);

}
