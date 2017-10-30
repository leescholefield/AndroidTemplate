package com.scholefield.lee.androidtemplate.ui.recyclerview;

import com.lee.scholefield.androidtemplate.ui.BaseRecyclerViewAdapterImp;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
public class BaseRecyclerViewAdapterTest {

    private BaseRecyclerViewAdapterImp classUnderTest;

    private List<String> defaultItems;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        defaultItems = new ArrayList<>();
        defaultItems.add("first");
        defaultItems.add("second");

        classUnderTest = new BaseRecyclerViewAdapterImp(defaultItems);
    }

    @Test
    public void insertItem_inserts_into_list() throws Exception {
        classUnderTest.insertItem("inserted");

        assertEquals(3, classUnderTest.getItemCount());
    }

    @Test
    public void insertItem_with_position() throws Exception {
        classUnderTest.insertItem("inserted", 1); // should be middle

        assertEquals("inserted", classUnderTest.getDataSet().get(1));
    }

    @Test
    public void insertItem_with_invalid_position_throws_runtime_exception() throws Exception {
        expectedException.expect(RuntimeException.class);

        classUnderTest.insertItem("inserted", -1);
    }

    @Test
    public void insertItem_with_position_END() throws Exception {
        classUnderTest.insertItem("inserted", BaseRecyclerViewAdapterImp.END);

        assertEquals(3, classUnderTest.getItemCount());
        assertEquals("inserted", classUnderTest.getDataSet().get(2));

        classUnderTest.insertItem("second inserted", BaseRecyclerViewAdapterImp.END);

        assertEquals(4, classUnderTest.getItemCount());
        assertEquals("second inserted", classUnderTest.getDataSet().get(3));
    }

    @Test
    public void insertItem_with_position_START() throws Exception {
        classUnderTest.insertItem("inserted", BaseRecyclerViewAdapterImp.START);

        assertEquals(3, classUnderTest.getItemCount());
        assertEquals("inserted", classUnderTest.getDataSet().get(0));
    }

    @Test
    public void removeItem_with_position_removes_item() throws Exception {
        classUnderTest.removeItem(1);

        assertEquals(1, classUnderTest.getItemCount());
    }

    @Test
    public void removeItem_with_obj_removes_item() throws Exception {
        classUnderTest.removeItem("first");

        assertEquals(1, classUnderTest.getItemCount());
        assertEquals("second", classUnderTest.getDataSet().get(0));
    }

}