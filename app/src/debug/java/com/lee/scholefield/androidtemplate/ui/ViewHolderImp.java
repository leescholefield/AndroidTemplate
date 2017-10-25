package com.lee.scholefield.androidtemplate.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.scholefield.lee.androidtemplate.R;

/**
 *
 */
public class ViewHolderImp extends RecyclerView.ViewHolder {

    TextView textView;

    ViewHolderImp(View view) {
        super(view);
        textView = (TextView) view.findViewById(R.id.rv_text_view);
    }

    public TextView getTextView() {
        return textView;
    }
}
