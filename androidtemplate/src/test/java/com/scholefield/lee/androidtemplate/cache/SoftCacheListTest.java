package com.scholefield.lee.androidtemplate.cache;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Tests the {@link SoftCache} with the item as a List
 */
public class SoftCacheListTest {

    private SoftCache<String, List<String>> classUnderTest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        classUnderTest = new SoftCache<>(3);
    }

    @Test
    public void put_increments_currentSize_by_inserted_list_size() throws Exception {
        List<String> toInsert = new ArrayList<>();
        toInsert.add("first");
        toInsert.add("second");

        classUnderTest.put("key", toInsert);

        assertEquals(2, classUnderTest.size());
    }

    @Test
    public void put_removes_oldest_value_when_currentSize_reached() throws Exception {
        List<String> first = new ArrayList<>();
        first.add("first");
        classUnderTest.put("first", first);
        List<String> second = new ArrayList<>();
        second.add("first");second.add("second");second.add("fourth");

        classUnderTest.put("second", second);

        assertEquals(3, classUnderTest.size());
    }

    @Test
    public void put_throws_exception_when_list_size_greater_than_maxSize_and_dataSet_cannot_be_cleared() throws Exception {
        List<String> toInsert = new ArrayList<>();
        toInsert.add("first");
        toInsert.add("second");
        toInsert.add("third");
        toInsert.add("fourth");

        expectedException.expect(IllegalArgumentException.class);
        classUnderTest.put("key", toInsert);
    }
}
