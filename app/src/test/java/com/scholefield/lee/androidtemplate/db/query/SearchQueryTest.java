package com.scholefield.lee.androidtemplate.db.query;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class SearchQueryTest {

    @Test
    public void getQuery_with_just_table() throws Exception {
        String expected = "SELECT * FROM table1";

        SearchQuery query = new SearchQuery("table1", null);

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_table_and_selection() throws Exception {
        String expected = "SELECT * FROM table1 WHERE id='2'";

        SearchQuery query = new SearchQuery("table1", "id='2'");

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_table_selection_and_single_column() throws Exception {
        String expected = "SELECT name FROM table1 WHERE id='2'";

        SearchQuery query = new SearchQuery("table1", new String[]{"name"}, "id='2'");

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_table_selection_and_multiple_columns() throws Exception {
        String expected = "SELECT name, dob FROM table1 WHERE id='2'";

        SearchQuery query = new SearchQuery("table1", new String[]{"name", "dob"}, "id='2'");

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_table_columns_and_null_selection() throws Exception {
        String expected = "SELECT name, dob FROM table1";

        SearchQuery query = new SearchQuery("table1", new String[]{"name", "dob"}, null);

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getColumns_returns_empty_array_when_columns_not_set() throws Exception {
        SearchQuery query = new SearchQuery("table1", null, null);

        assertEquals(0, query.getColumns().length);
    }

    @Test
    public void getColumns_returns_array_of_columns_when_set() throws Exception {
        SearchQuery query = new SearchQuery("table1", new String[]{"name", "dob"}, null);

        assertEquals(2, query.getColumns().length);
    }


}