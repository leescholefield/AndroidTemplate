package com.scholefield.lee.androidtemplate.ui.recyclerview;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.lee.scholefield.androidtemplate.ui.DummyActivity;
import com.lee.scholefield.androidtemplate.ui.BaseRecyclerViewAdapterImp;
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
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class BaseRecyclerViewAdapterUiTest {

    // a simple implementation of BaseRecyclerViewAdapter
    private BaseRecyclerViewAdapterImp classUnderTest;
    private RecyclerView recyclerView;

    private BaseRecyclerViewAdapter.Callback<String> mockedCallback;

    @Rule
    public ActivityTestRule<DummyActivity> activityTestRule = new ActivityTestRule<>(DummyActivity.class);

    private List<String> createdList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {

        mockedCallback = mock(BaseRecyclerViewAdapter.Callback.class);

        final DummyActivity activity = activityTestRule.getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // create adapter
                createdList.add("first");
                createdList.add("second");
                classUnderTest = new BaseRecyclerViewAdapterImp(createdList);
                classUnderTest.setCallback(mockedCallback);

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
    public void click_on_items_triggers_callback() throws Exception {
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.click()));

        verify(mockedCallback).onItemClicked("second", 1);
    }

    @Test
    public void longClick_on_item_triggers_callback() throws Exception {
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.longClick()));

        verify(mockedCallback).onItemLongClicked("first", 0);
    }

    @Test
    public void adding_item_to_adapter_updates_recycler_view() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classUnderTest.insertItem("inserted");
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder = (ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(2);

        assertNotNull(viewHolder);
        assertEquals("inserted", viewHolder.getTextView().getText());
    }

    @Test
    public void adding_item_to_start_of_adapter_updates_recycler_view() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classUnderTest.insertItem("inserted", BaseRecyclerViewAdapterImp.START);
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder =(ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(0);

        assertNotNull(viewHolder);
        assertEquals("inserted", viewHolder.getTextView().getText());
    }

    @Test
    public void adding_item_to_middle_of_adapter_updates_recycler_view() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classUnderTest.insertItem("inserted", 1);
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder =(ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(1);

        assertNotNull(viewHolder);
        assertEquals("inserted", viewHolder.getTextView().getText());
    }

    @Test
    public void removing_item_by_position_from_adapter_updates_recycler_view() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classUnderTest.removeItem(1);
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder = (ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(1);

        assertNull(viewHolder);
    }

    @Test
    public void removing_item_from_adapter_updates_recycler_view() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classUnderTest.removeItem("second");
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        ViewHolderImp viewHolder = (ViewHolderImp)recyclerView.findViewHolderForAdapterPosition(1);

        assertNull(viewHolder);
    }



}