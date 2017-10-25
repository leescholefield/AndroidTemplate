package com.scholefield.lee.androidtemplate.ui;


/**
 * An implementation of {@link BaseContract.BasePresenter} to handle interactions with the {@link PresenterManager}.
 *
 * When the Presenter is instantiated it registers itself with the {@code PresenterManager}. This allows to view to
 * re-obtain an instance after an orientation change. When the view is being permanently destroyed it should call
 * {@link #deregisterWithManager()} to avoid any memory leaks.
 */
public abstract class BasePresenterImpl implements BaseContract.BasePresenter {

    /**
     * Key this presenter is saved under in the {@link PresenterManager}.
     */
    private final String managerKey;

    /**
     * Protected constructor. This will automatically call {@link #registerWithManager()}.
     */
    protected BasePresenterImpl(String managerKey) {
        this.managerKey = managerKey;

        registerWithManager();
    }

    /**
     * Presenter should deregister with the {@link PresenterManager}.
     *
     * Should be called when the View is being permanently destroyed.
     */
    @Override
    public void deregisterWithManager() {
        PresenterManager.getInstance().deregisterPresenter(managerKey);
    }

    /**
     * Registers this presenter with the {@link PresenterManager} using {@link #managerKey}.
     */
    @Override
    public void registerWithManager() {
        PresenterManager.getInstance().registerPresenter(managerKey, this);
    }

    protected String getManagerKey(){return managerKey;}
}
