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
    public void getQuery_with_all_values_set() throws Exception {
        MultitableSearchQuery query = new MultitableSearchQuery.Builder("firstTable")
                .table("secondTable", "firstTable.id = secondTable.id")
                .columns(new String[]{"id", "name", "age"})
                .where("firstTable.age > 20")
                .build();

        String expected = "SELECT id, name, age FROM firstTable INNER JOIN secondTable ON firstTable.id = secondTable.id " +
                "WHERE firstTable.age > 20";

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_column_not_set() throws Exception {
        MultitableSearchQuery query = new MultitableSearchQuery.Builder("firstTable")
                .table("secondTable", "firstTable.id = secondTable.id")
                .where("firstTable.age > 20")
                .build();

        String expected = "SELECT * FROM firstTable INNER JOIN secondTable ON firstTable.id = secondTable.id " +
                "WHERE firstTable.age > 20";

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_where_not_set() throws Exception {
        MultitableSearchQuery query = new MultitableSearchQuery.Builder("firstTable")
                .table("secondTable", "firstTable.id = secondTable.id")
                .columns(new String[]{"id", "name", "age"})
                .build();

        String expected = "SELECT id, name, age FROM firstTable INNER JOIN secondTable ON firstTable.id = secondTable.id";

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_three_tables() throws Exception {
        MultitableSearchQuery query = new MultitableSearchQuery.Builder("firstTable")
                .table("secondTable", "firstTable.id = secondTable.id")
                .table("thirdTable", "firstTable.id = thirdTable.id")
                .columns(new String[]{"id", "name", "age"})
                .where("firstTable.age > 20")
                .build();

        String expected = "SELECT id, name, age FROM firstTable INNER JOIN secondTable ON firstTable.id = secondTable.id " +
                "INNER JOIN thirdTable ON firstTable.id = thirdTable.id WHERE firstTable.age > 20";

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void tablesToString_with_one_join() throws Exception {
        MultitableSearchQuery query = new MultitableSearchQuery.Builder("firstTable")
                .table("secondTable", "firstTable.id = secondTable.id").build();

        String expected = "FROM firstTable INNER JOIN secondTable ON firstTable.id = secondTable.id";

        assertEquals(expected, query.tablesToString());
    }

    @Test
    public void tablesToString_with_two_joins() throws Exception {
        MultitableSearchQuery query = new MultitableSearchQuery.Builder("firstTable")
                .table("secondTable", "firstTable.id = secondTable.id")
                .table("thirdTable", "firstTable.id = thirdTable.id").build();

        String expected = "FROM firstTable INNER JOIN secondTable ON firstTable.id = secondTable.id " +
                "INNER JOIN thirdTable ON firstTable.id = thirdTable.id";

        assertEquals(expected, query.tablesToString());
    }


}