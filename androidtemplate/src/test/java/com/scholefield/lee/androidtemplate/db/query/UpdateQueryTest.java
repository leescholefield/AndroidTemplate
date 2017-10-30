package com.scholefield.lee.androidtemplate.db.query;

import android.content.ContentValues;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 *
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class UpdateQueryTest {

    @Test
    public void getQuery_with_table_and_single_newValue() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("age", 26);
        UpdateQuery query = new UpdateQuery("users", cv, null);

        String expected = "UPDATE users SET age = 26";

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_table_and_multiple_newValues() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("age", 26);
        cv.put("name", "lee");

        UpdateQuery query = new UpdateQuery("users", cv, null);

        String expected = "UPDATE users SET name = 'lee', age = 26";

        assertEquals(expected, query.getQuery());
    }

    @Test
    public void getQuery_with_where_clause() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("age", 26);
        UpdateQuery query = new UpdateQuery("users", cv, "id = 7");

        String expected = "UPDATE users SET age = 26 WHERE id = 7";

        assertEquals(expected, query.getQuery());
    }


    @Test
    public void newValuesToString_correctly_formats_string() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("column", "value");
        UpdateQuery query = new UpdateQuery(null, cv, null);

        String expected = "column = 'value'";

        assertEquals(expected, query.newValuesToString());
    }

    @Test
    public void newValuesToString_correctly_formats_int() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("age", 20);
        UpdateQuery query = new UpdateQuery(null, cv, null);

        String expected = "age = 20";

        assertEquals(expected, query.newValuesToString());
    }

    @Test
    public void newValuesToString_correctly_formats_double() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("salary", 200.01);
        UpdateQuery query = new UpdateQuery(null, cv, null);

        String expected = "salary = 200.01";

        assertEquals(expected, query.newValuesToString());
    }

    @Test
    public void newValuesToString_comma_separates_all_but_last_value() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("column", "value");
        cv.put("secondColumn", "secondValue");
        UpdateQuery query = new UpdateQuery(null, cv, null);

        String expected = "column = 'value', secondColumn = 'secondValue'";

        assertEquals(expected, query.newValuesToString());
    }

}