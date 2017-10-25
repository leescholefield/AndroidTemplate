package com.scholefield.lee.androidtemplate.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Simple Activity implementation of {@link BaseContract.BaseView}. This view will manage the lifecycle of the associated
 * presenter.
 */
public abstract class BaseActivityImpl<T extends BaseContract.BasePresenter> extends AppCompatActivity
        implements BaseContract.BaseView {

    protected T presenter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

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
