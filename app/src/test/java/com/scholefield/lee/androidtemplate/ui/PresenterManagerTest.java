package com.scholefield.lee.androidtemplate.ui;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class PresenterManagerTest {

    private PresenterManager classUnderTest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        classUnderTest = new PresenterManager();
    }

    @Test
    public void getPresenter_returns_null_when_active_presenters_empty() throws Exception {
        assertNull(classUnderTest.getPresenter("non-existent"));
    }

    @Test
    public void getPresenter_returns_presenter_when_already_registered() throws Exception {
        classUnderTest.registerPresenter("key", createPresenter());

        assertNotNull(classUnderTest.getPresenter("key"));
    }

    @Test
    public void registerPresenter_saves_presenter_in_active_presenters() throws Exception {
        classUnderTest.registerPresenter("key", createPresenter());

        Map<String, BaseContract.BasePresenter> presenters = classUnderTest.getActivePresenters();

        assertEquals(1, presenters.size());
        assertNotNull(presenters.get("key"));
    }

    @Test
    public void registerPresenter_throws_exception_when_key_is_null() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        classUnderTest.registerPresenter(null, createPresenter());
    }

    @Test
    public void registerPresenter_throws_exception_when_presenter_is_null() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        classUnderTest.registerPresenter("key", null);
    }

    @Test
    public void getInstance_returns_same_instance() throws Exception {
        classUnderTest.registerPresenter("key", createPresenter());

        PresenterManager manager = PresenterManager.getInstance();

        assertNotNull(manager.getPresenter("key"));
    }

    @Test
    public void deregisterPresenter_removes_presenter_from_active_presenters() throws Exception {
        classUnderTest.registerPresenter("key", createPresenter());
        assertNotNull(classUnderTest.getPresenter("key"));

        classUnderTest.deregisterPresenter("key");

        assertNull(classUnderTest.getPresenter("key"));
    }

    private BaseContract.BasePresenter createPresenter() {
        return new BaseContract.BasePresenter() {

            @Override
            public void deregisterWithManager() {}

            @Override
            public void registerWithManager() {
            }
        };
    }

}