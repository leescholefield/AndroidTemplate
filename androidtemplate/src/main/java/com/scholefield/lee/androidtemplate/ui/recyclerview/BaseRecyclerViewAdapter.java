package com.scholefield.lee.androidtemplate.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Base implementation of {@link RecyclerView.Adapter}. This is mainly responsible for managing the data set.
 *
 * Subclasses need to implement the {@link #onCreateViewHolder} and {@link #onBindViewHolder} methods.
 *
 * This also sets an on click listener on the ViewHolder. By default this will call {@link Callback#onItemClicked} or
 * {@link Callback#onItemLongClicked} with the clicked {@code <T>} and its position in the data set. To provide a custom
 * implementation override {@link #handleOnClick} and {@link #handleOnLongClick}.
 *
 * @param <T> the type of object this recyclerView displays.
 * @param <VH> ViewHolder subclass.
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected List<T> dataSet;

    /**
     * Callback used to communicate with the hosting Fragment/Activity
     */
    protected Callback<T> callback;

    /**
     * Use as the index for {@link #insertItem} or {@link #removeItem}.
     */
    public static int START = -3;

    /**
     * Use as the index for {@link #insertItem} or {@link #removeItem}.
     */
    public static int END = -5;

    /**
     * Public constructor.
     *
     * @param items a list of {@code T} that is displayed in the recycler view.
     */
    public BaseRecyclerViewAdapter(List<T> items) {
        this.dataSet = items;
    }

    /**
     * Sets the callback used to communicate with the holding Fragment/Activity.
     *
     * Note, a callback is not required to use this class.
     */
    public void setCallback(Callback<T> callback) {
        this.callback = callback;
    }

    /**
     * Callback used to communicate with the holding Fragment/Activity. To set the callback call {@link #setCallback}.
     */
    public interface Callback<T> {
        void onItemClicked(T item, int position);

        void onItemLongClicked(T item, int position);
    }

    /**
     * Sets an onClick listener on the {@code holder}.
     */
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnClick(holder, v);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return handleOnLongClick(holder, v);
            }
        });
    }

    /**
     * Override to provide a custom long click handler on a ViewHolder. By default this will call {@link Callback#onItemLongClicked}.
     *
     * @param holder view holder parent
     * @param view actual view that was clicked on.
     * @return {@code true} if the click is handled. This is to stop the RecyclerView calling its own implementation.
     */
    protected boolean handleOnLongClick(VH holder, View view) {
        int position = holder.getAdapterPosition();

        if (callback != null) {
            callback.onItemLongClicked(dataSet.get(position), position);
        }

        return true;
    }

    /**
     * Override to provide a custom on click handler. By default this will call {@link Callback#onItemClicked}.
     *
     * @param holder view holder parent.
     * @param view actual view that was clicked on.
     */
    protected void handleOnClick(VH holder, View view) {
        int position = holder.getAdapterPosition();

        if (callback != null) {
            callback.onItemClicked(dataSet.get(position), position);
        }
    }

    /**
     * Inserts the {@code item} into the data set at the given {@code index}, and then notifies the recyclerView of the change.
     *
     * @param item item to insert.
     * @param index index to insert at.
     */
    public void insertItem(T item, int index) {
        int position = index;

        if (position == START) {
            position = 0;
        } else if (position == END) {
            position = dataSet.size();
        }

        dataSet.add(position, item);

        notifyItemInserted(position);
    }

    /**
     * Inserts the {@code item} at the end of the data set, and then notifies the recyclerView of the change.
     *
     * @param item item to insert.
     */
    public void insertItem(T item) {
        dataSet.add(item);
        notifyItemInserted(dataSet.size());
    }

    /**
     * Removes the item at the given {@code position} from the data set, and then notifies the recyclerView of the change.
     *
     * @param position position of the item to remove.
     * @return the removed item.
     */
    public T removeItem(int position) {
        int pos = position;
        if (pos == START) {
            pos = 0;
        } else if (pos == END) {
            pos = dataSet.size();
        }

        T removed = dataSet.remove(pos);
        notifyItemRemoved(pos);
        return removed;
    }

    /**
     * Removes the given {@code item} from the data set, and then notifies the recyclerView of the change.
     *
     * @param item item to remove.
     */
    public void removeItem(T item) {
        int pos = dataSet.indexOf(item);
        dataSet.remove(item);

        if (pos != -1) {
            notifyItemRemoved(pos);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    protected List<T> getDataSet() {
        return dataSet;
    }

}