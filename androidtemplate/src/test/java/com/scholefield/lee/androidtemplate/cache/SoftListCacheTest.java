package com.scholefield.lee.androidtemplate.cache;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class SoftListCacheTest {

    private SoftListCache<String, String> classUnderTest;

    @Before
    public void setUp() throws Exception {
        classUnderTest = new SoftListCache<>(3);
    }

    @Test
    public void remove_removes_items_from_data_set() throws Exception {
        insertTestValuesIntoDataSet("toRemove");

        classUnderTest.remove("toRemove");

        assertNull(classUnderTest.get("toRemove"));
    }

    @Test
    public void remove_decreases_currentSize_by_size_of_removed_list() throws Exception {
        insertTestValuesIntoDataSet("toRemove");

        classUnderTest.remove("toRemove");

        assertEquals(-2, classUnderTest.size());
    }

    @Test
    public void removeAll_clears_cache() throws Exception {
        insertTestValuesIntoDataSet("first");
        insertTestValuesIntoDataSet("second");

        classUnderTest.removeAll();

        assertTrue(classUnderTest.getDataSet().isEmpty());
    }

    @Test
    public void removeAll_resets_currentSize_to_0() throws Exception {
        insertTestValuesIntoDataSet("first");
        insertTestValuesIntoDataSet("second");

        classUnderTest.removeAll();

        assertEquals(0, classUnderTest.size());
    }

    @Test
    public void removeSingle_successfully_removes_item() throws Exception {
        insertTestValuesIntoDataSet("key");

        classUnderTest.removeSingle("first");

        assertFalse(classUnderTest.get("key").contains("first"));
    }

    @Test
    public void removeSingle_decreases_currentSize_by_1_when_removed() throws Exception {
        insertTestValuesIntoDataSet("key");
        int currentSize = classUnderTest.size();

        classUnderTest.removeSingle("second");

        assertEquals(currentSize - 1, classUnderTest.size());
    }

    @Test
    public void removeSingle_does_nothing_when_given_nonexistant_item() throws Exception {
        insertTestValuesIntoDataSet("key");
        int currentSize = classUnderTest.size();

        classUnderTest.removeSingle("not-exist");

        assertTrue(classUnderTest.size() == currentSize);
    }

    private void insertTestValuesIntoDataSet(String key) {
        List<String> toInsert = new ArrayList<>();
        toInsert.add("first");
        toInsert.add("second");
        Map<String, List<String>> dataSet = classUnderTest.getDataSet();
        dataSet.put(key, toInsert);
    }


}