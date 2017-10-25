package com.scholefield.lee.androidtemplate.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Simple Fragment base implementation of {@link BaseContract.BaseView}. This will manage the lifecycle of the associated
 * presenter.
 */
public abstract class BaseFragmentImpl<T extends BaseContract.BasePresenter> extends Fragment implements BaseContract.BaseView {

    protected T presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = getPresenter();
    }

    /**
     * This will call {@link #onFragmentDestroyed()} if the hosting Activity is finishing or this fragment is being destroyed.
     */
    @Override
    public void onPause() {

        if(getActivity().isFinishing() || isRemoving() || isDetached()) {
            onFragmentDestroyed();
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
     * Returns the key the manager is saved under in the {@link PresenterManager}.
     */
    protected abstract String getPresenterKey();

    /**
     * Creates a new Presenter if it was not found withing the {@link PresenterManager}.
     */
    protected abstract T createPresenter();

    /**
     * Called when the Fragment (or its hosting Activity) is being permanently destroyed. All cleanup should be done in this
     * method.
     *
     * By default this will call {@link BaseContract.BasePresenter#deregisterWithManager()}.
     */
    protected void onFragmentDestroyed() {
        presenter.deregisterWithManager();
    }
}
