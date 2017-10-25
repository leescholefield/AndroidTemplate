package com.scholefield.lee.androidtemplate.usecase;

/**
 * This is responsible for executing {@link UseCase}s via a {@link UseCaseScheduler}.
 *
 * todo need some way to attach a callback to active use cases in-case of orientation change.
 */
public class UseCaseHandler {

    private static UseCaseHandler INSTANCE;

    /**
     * Handles thread execution and communication with the UI.
     */
    private final UseCaseScheduler scheduler;

    /**
     * Package-private constructor for testing. Use {@link #getInstance()} to get a reference.
     */
    UseCaseHandler(UseCaseScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Gets the singleton class reference.
     */
    public static UseCaseHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UseCaseHandler(new UseCaseThreadPoolScheduler());
        }
        return INSTANCE;
    }

    /**
     * Executes the {@link UseCase#run()} method.
     *
     * @param useCase {@link UseCase} to execute.
     * @param values {@code UseCase.RequestValues} for the specific use case.
     * @param callback to the caller.
     * @param <V> {@code RequestValues} for the {@code UseCase}.
     * @param <R> {@code ResponseValues} for the {@code UseCase}.
     */
    public <V extends UseCase.RequestValues, R extends UseCase.ResponseValue> void execute(
            final UseCase<V,R> useCase, V values, UseCase.UseCaseCallback<R> callback) {

        useCase.setRequestValues(values);
        // wrap the callback to intercept calls.
        useCase.setUseCaseCallback(new UiCallbackWrapper<>(callback, this));

        scheduler.execute(new Runnable() {
            @Override
            public void run() {
                useCase.run();
            }
        });
    }

    /**
     * Notify the {@link UseCaseScheduler} that a response has been received. This will then call
     * {@link UseCase.UseCaseCallback#onSuccess} on the UiThread.
     *
     * @param response {@code ResponseValue} received from the UseCase
     * @param callback  the callback passed to the {@link UseCaseHandler#execute} method.
     */
    <V extends UseCase.ResponseValue> void notifyResponse(final V response, final UseCase.UseCaseCallback<V> callback) {
        scheduler.notifyResponse(response, callback);
    }

    /**
     * Notify the {@link UseCaseScheduler} that the {@code UseCase} encountered an error during execution. This will then call
     * {@link UseCase.UseCaseCallback#onError()} on the UI thread.
     *
     * @param callback the callback passed to the {@link UseCaseHandler#execute} method.
     */
    <V extends UseCase.ResponseValue> void notifyError(final UseCase.UseCaseCallback<V> callback) {
        scheduler.onError(callback);
    }

    /**
     * Passed to the {@link UseCase#setUseCaseCallback} method. This intercepts {@link UseCase.UseCaseCallback} calls from the
     * {@code UseCase} internal implementation and passes them to {@link #notifyResponse} or {@link #notifyError}.
     */
    private static final class UiCallbackWrapper<V extends UseCase.ResponseValue> implements
            UseCase.UseCaseCallback<V> {

        /**
         * The actual {@code UseCaseCallback} created by the {@code UseCaseHandler} caller.
         */
        private final UseCase.UseCaseCallback<V> callback;

        private final UseCaseHandler handler;

        /**
         * Package private constructor to avoid instantiation.
         *
         * @param callback callback passed to {@link UseCaseHandler#execute}
         * @param handler {@code UseCaseHandler} instance.
         */
        UiCallbackWrapper(UseCase.UseCaseCallback<V> callback, UseCaseHandler handler) {
            this.callback = callback;
            this.handler = handler;
        }

        @Override
        public void onSuccess(V response) {

            handler.notifyResponse(response, callback);
        }

        @Override
        public void onError() {
            handler.notifyError(callback);
        }

    }

}
