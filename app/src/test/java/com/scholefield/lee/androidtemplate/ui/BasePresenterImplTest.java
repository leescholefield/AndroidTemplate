package com.scholefield.lee.androidtemplate.ui;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class BasePresenterImplTest {

    @Test
    public void constructor_will_register_with_manager() throws Exception {
        new PresenterImpl("key");

        PresenterManager manager = PresenterManager.getInstance();
        assertNotNull(manager.getPresenter("key"));

        manager.deregisterPresenter("key");
    }

    @Test
    public void deregisterWithManager_successfully_removes_presenter_from_manager() throws Exception {
        BasePresenterImpl presenter = new PresenterImpl("key");

        presenter.deregisterWithManager();

        PresenterManager manager = PresenterManager.getInstance();
        assertNull(manager.getPresenter("key"));
    }

    /**
     * Since {@code BasePresenterImpl} is abstract we just need a subclass so we can instantiate it.
     */
    private class PresenterImpl extends BasePresenterImpl {
        private PresenterImpl(String key) {
            super(key);
        }
    }

}