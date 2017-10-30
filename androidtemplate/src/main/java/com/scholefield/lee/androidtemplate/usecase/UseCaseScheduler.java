package com.scholefield.lee.androidtemplate.usecase;

/**
 * A scheduler is responsible for executing a {@link UseCase}
 */
public interface UseCaseScheduler {

    void execute(Runnable runnable);

    /**
     * Called when the {@code UseCase} has successfully executed.
     *
     * @param response {@link UseCase.ResponseValue}
     * @param callback callback used to communicate with the UI thread.
     */
    <V extends UseCase.ResponseValue> void notifyResponse(final V response, final UseCase.UseCaseCallback<V> callback);

    /**
     * Called when the {@code UseCase} encounters an error during execution.
     *
     * @param callback callback used to communicate with the UI thread.
     */
    <V extends UseCase.ResponseValue> void onError(final UseCase.UseCaseCallback<V> callback);
}
