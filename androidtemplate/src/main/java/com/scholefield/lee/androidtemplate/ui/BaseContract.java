package com.scholefield.lee.androidtemplate.ui;

/**
 * Defines the base contract between a Presenter and a View. All BaseFragament/Presenter/Activity subclasses should implement
 * the corresponding interface.
 */
public interface BaseContract {

    /*
    * Currently just a stub, however BaseFragment/Activity still implements this in case we need to add functionality in
     * the future.
    */
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
