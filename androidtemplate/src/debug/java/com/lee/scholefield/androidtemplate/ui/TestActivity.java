package com.lee.scholefield.androidtemplate.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.scholefield.lee.androidtemplate.R;
import com.scholefield.lee.androidtemplate.ui.recyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestActivity extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        System.out.println("onCreate called");

        rv = (RecyclerView)findViewById(R.id.recycler_view);

        List<String> dataSet = new ArrayList<>();
        dataSet.add("first");
        dataSet.add("second");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(linearLayoutManager);

        TouchRecyclerViewAdapterImp adapter = new TouchRecyclerViewAdapterImp(dataSet);
        rv.setAdapter(adapter);
    }
}
