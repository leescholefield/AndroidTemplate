package com.scholefield.lee.androidtemplate.ui.recyclerview;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.lee.scholefield.androidtemplate.ui.DummyActivity;
import com.lee.scholefield.androidtemplate.ui.TouchRecyclerViewAdapterImp;
import com.lee.scholefield.androidtemplate.ui.ViewHolderImp;
import com.scholefield.lee.androidtemplate.R;
import com.scholefield.lee.androidtemplate.RecyclerViewActions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class TouchRecyclerViewAdapterUiTest {

    private TouchRecyclerViewAdapterImp classUnderTest;
    private RecyclerView recyclerView;

    @Rule
    public ActivityTestRule<DummyActivity> activityTestRule = new ActivityTestRule<>(DummyActivity.class);

    @Before
    public void setUp() throws Exception {
        final Activity activity = activityTestRule.getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<String> items = new ArrayList<>();
                items.add("first");
                items.add("second");
                classUnderTest = new TouchRecyclerViewAdapterImp(items);

                // create recyclerview
                recyclerView = new RecyclerView(activity);
                recyclerView.setId(R.id.recycler_view);
                activity.setContentView(recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                recyclerView.setAdapter(classUnderTest);
            }
        });

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    @Test
    public void onItemMove_moves_position_in_recyclerView() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classUnderTest.onItemMove(0, 1);
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder =(ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(1);

        assertEquals("first", viewHolder.getTextView().getText());
    }

    @Test
    public void swipe_left_calls_onSwipeLeft() throws Exception {
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.swipeLeft()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder =(ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(1);

        assertNull(viewHolder);
    }

    @Test
    public void swipe_right_calls_onSwipeRight() throws Exception {
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.swipeRight()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder =(ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(1);

        assertNull(viewHolder);
    }

}