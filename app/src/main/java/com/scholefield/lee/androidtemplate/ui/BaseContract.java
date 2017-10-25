package com.scholefield.lee.androidtemplate.ui;

/**
 * Defines the base contract between a Presenter and a View.
 */
public interface BaseContract {

    interface BaseView {
    }

    interface BasePresenter {

        /**
         * Presenter should deregister with the {@link PresenterManager}.
         *
         * Should be called when the View is being permanently destroyed.
         */
        void deregisterWithManager();

        /**
         * Register the presenter with the {@link PresenterManager}.
         */
        void registerWithManager();
    }
}
