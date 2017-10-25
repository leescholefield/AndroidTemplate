package com.scholefield.lee.androidtemplate.db.query;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class DeleteQueryTest {

    @Test
    public void getQuery_with_just_table() throws Exception {
        String expected = "DELETE FROM table1";

        String actual = new DeleteQuery("table1").getQuery();

        assertEquals(expected, actual);
    }

    @Test
    public void getQuery_with_table_and_where() throws Exception {
        String expected = "DELETE FROM table1 WHERE name='lee'";

        String actual = new DeleteQuery("table1", "name='lee'").getQuery();

        assertEquals(expected, actual);
    }

}