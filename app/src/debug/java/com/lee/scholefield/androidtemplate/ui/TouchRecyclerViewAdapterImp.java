package com.lee.scholefield.androidtemplate.ui;

import android.graphics.*;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.scholefield.lee.androidtemplate.R;
import com.scholefield.lee.androidtemplate.ui.recyclerview.TouchRecyclerViewAdapter;

import java.util.List;

/**
 *
 */
public class TouchRecyclerViewAdapterImp extends TouchRecyclerViewAdapter<String, ViewHolderImp> {

    public TouchRecyclerViewAdapterImp(List<String> items) {
        super(items);
    }

    @Override
    public void onBindViewHolder(ViewHolderImp holder, int position) {
        super.onBindViewHolder(holder, position);

        String item = dataSet.get(position);
        holder.textView.setText(item);
    }

    @Override
    public ViewHolderImp onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_view_holder, parent, false);
        return new ViewHolderImp(view);
    }

    @Override
    public void animateRightSwipe(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY) {
        Paint p = new Paint();
        p.setARGB(255, 249, 28, 28); // red
        View itemView = viewHolder.itemView;
        canvas.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                    (float) itemView.getBottom(), p);
        itemView.setTranslationX(dX / 1.5f);
    }

    @Override
    public void animateLeftSwipe(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY) {
        Paint p = new Paint();
        p.setARGB(255, 73, 40, 240); // blue
        View itemView = viewHolder.itemView;
        canvas.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(),
                (float) itemView.getBottom(), p);
        itemView.setTranslationX(dX / 1.5f);
    }

    @Override
    public void onSwipeLeft(int position) {
        removeItem(position);
    }

    @Override
    public void onSwipeRight(int position) {
        removeItem(position);
    }
}
