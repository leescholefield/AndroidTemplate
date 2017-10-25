package com.lee.scholefield.androidtemplate.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.scholefield.lee.androidtemplate.R;
import com.scholefield.lee.androidtemplate.ui.recyclerview.BaseRecyclerViewAdapter;

import java.util.List;

/**
 *
 */
public class BaseRecyclerViewAdapterImp extends BaseRecyclerViewAdapter<String, ViewHolderImp> {

    public BaseRecyclerViewAdapterImp(List<String> items) {
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

}
