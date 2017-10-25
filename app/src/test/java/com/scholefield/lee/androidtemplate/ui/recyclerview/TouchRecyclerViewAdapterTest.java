package com.scholefield.lee.androidtemplate.ui.recyclerview;

import com.lee.scholefield.androidtemplate.ui.BaseRecyclerViewAdapterImp;
import com.lee.scholefield.androidtemplate.ui.TouchRecyclerViewAdapterImp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TouchRecyclerViewAdapterTest {

    private TouchRecyclerViewAdapterImp classUnderTest;

    @Before
    public void setUp() throws Exception {
        List<String> defaultItems = new ArrayList<>();
        defaultItems.add("first");
        defaultItems.add("second");

        classUnderTest = new TouchRecyclerViewAdapterImp(defaultItems);
    }

    @Test
    public void moveItem_swaps_item_in_adapter() throws Exception {
        classUnderTest.onItemMove(0, 1);

        assertEquals("first", classUnderTest.getDataSet().get(1));
    }



}