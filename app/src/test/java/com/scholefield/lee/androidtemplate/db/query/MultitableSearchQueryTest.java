package com.scholefield.lee.androidtemplate.db.query;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 *
 */
public class MultitableSearchQueryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getQuery_with_tables_and_on_clause() throws Exception {
        String expected = "SELECT * FROM table1 JOIN table2 ON table1.id = table2.id";

        String actual = new MultitableSearchQuery(new String[]{"table1", "table2"}, "table1.id = table2.id").getQuery();

        assertEquals(expected, actual);
    }

    @Test
    public void getQuery_with_where_clause() throws Exception {
        String expected = "SELECT * FROM table1 JOIN table2 ON table1.id = table2.id WHERE table1.id = 1";

        String actual = new MultitableSearchQuery(new String[]{"table1", "table2"}, "table1.id = table2.id",
                "table1.id = 1").getQuery();

        assertEquals(expected, actual);
    }

    @Test
    public void passing_an_array_with_only_one_element_to_constructor_throws_exception() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        new MultitableSearchQuery(new String[]{"table"}, "on clause");
    }

    @Test
    public void getQuery_with_columns() throws Exception {
        String expected = "SELECT name, dob FROM table1 JOIN table2 ON table1.id = table2.id WHERE table1.id = 1";

        String actual = new MultitableSearchQuery(new String[]{"table1", "table2"}, "table1.id = table2.id",
                "table1.id = 1", new String[]{"name", "dob"}).getQuery();

        assertEquals(expected, actual);
    }


}