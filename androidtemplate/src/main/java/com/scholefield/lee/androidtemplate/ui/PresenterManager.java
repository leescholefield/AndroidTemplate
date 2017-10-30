package com.scholefield.lee.androidtemplate.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores active Presenters so they can be retrieved when the view is recreated.
 */
public class PresenterManager {

    private static PresenterManager INSTANCE;

    private Map<String, BaseContract.BasePresenter> activePresenters;

    /**
     * Gets the current Instance, or creates one if it is null.
     */
    public static PresenterManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PresenterManager();
        }
        return INSTANCE;
    }

    /**
     * Package-private constructor used for testing. Use {@link #getInstance()} in production.
     */
    PresenterManager() {
        activePresenters = new HashMap<>();
        INSTANCE = this;
    }

    /**
     * Returns the {@code BasePresenter} from the active presenters matching the {@code key}, or null if no match is found.
     */
    public BaseContract.BasePresenter getPresenter(String key) {
        return activePresenters.get(key);
    }

    /**
     * Registers the {@code presenter} under the given {@code key} in the Map of active presenters.
     */
    public void registerPresenter(String key, BaseContract.BasePresenter presenter) {
        if(key == null || presenter == null) {
            throw new IllegalArgumentException("key == null || presenter == null");
        }
        activePresenters.put(key, presenter);
    }

    public void deregisterPresenter(String key) {
        activePresenters.remove(key);
    }

    /**
     * Returns a map containing all the currently active presenters.
     */
    Map<String, BaseContract.BasePresenter> getActivePresenters() {
        return activePresenters;
    }
}
