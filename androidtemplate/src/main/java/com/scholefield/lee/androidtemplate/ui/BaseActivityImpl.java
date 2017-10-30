package com.scholefield.lee.androidtemplate.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Simple Activity implementation of {@link BaseContract.BaseView} that manages the lifecycle of the associated
 * presenter.
 *
 * The main lifecycle events will be managed in the {@link #onCreate(Bundle)} and {@link #onPause()} methods. All subclasses
 * should call {@code super} implementations when overriding these methods.
 *
 *      In {@link #onCreate} it will set the {@link #presenter} by first checking with the {@link PresenterManager} for any
 *      existing instances. If none exist it will then call {@link #createPresenter()}.
 *
 *      In {@link #onPause()} it will check if the activity is being permanently destroyed (via {@link #isFinishing()}. If
 *      so it will call {@link #onActivityDestroyed()} to perform any clean-up (by default this will deregister the presenter
 *      with the manager).
 *
 */
public abstract class BaseActivityImpl<T extends BaseContract.BasePresenter> extends AppCompatActivity
        implements BaseContract.BaseView {

    protected T presenter;

    /**
     * Sets the {@link #presenter}. Subclasses should always call super implementation.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getPresenter();
    }

    /**
     * This will call {@link #onActivityDestroyed()} if this activity is finishing.
     */
    @Override
    protected void onPause() {
        if(isFinishing()) {
            onActivityDestroyed();
        }
        super.onPause();
    }

    /**
     * Checks the {@link PresenterManager} for the Presenter saved under {@link #getPresenterKey()}. If none was found
     * {@link #createPresenter()} will be called.
     *
     * @return the presenter associated with this fragment.
     */
    protected T getPresenter() {
        @SuppressWarnings("unchecked")
        T presenter = (T)PresenterManager.getInstance().getPresenter(getPresenterKey());

        if (presenter == null) {
            presenter = createPresenter();
        }

        return presenter;
    }

    /**
     * Creates a new instance of {@code T}.
     */
    protected abstract T createPresenter();

    /**
     * Returns the key the presenter is saved under in the {@link PresenterManager}.
     */
    protected abstract String getPresenterKey();

    /**
     * Called when the Activity is being destroyed and will not be recreated. All cleanup should be done in this method.
     *
     * By default this will call {@link BaseContract.BasePresenter#deregisterWithManager()}.
     */
    protected void onActivityDestroyed() {
        presenter.deregisterWithManager();
    }
}
