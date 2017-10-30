package com.scholefield.lee.androidtemplate.usecase;


import android.os.Handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Uses a {@link ThreadPoolExecutor} to execute the {@link UseCase}s and a {@link Handler} to communicate with the Ui thread.
 */
public class UseCaseThreadPoolScheduler implements UseCaseScheduler {

    /**
     * Used to communicate with the UI thread.
     */
    private final Handler handler = new Handler();

    /**
     * Initial number of Threads the {@link ThreadPoolExecutor} should keep alive.
     */
    private static final int POOL_SIZE = 2;

    /**
     * Maximum number of Threads the {@link ThreadPoolExecutor} should create.
     */
    private static final int MAX_POOL_SIZE = 4;

    /**
     * Seconds excess (i.e. any pools over the {@link #POOL_SIZE}) Threads should be kept alive if they become idle.
     */
    private static final int TIMEOUT = 30;

    /**
     * Executes {@link UseCase}s in a separate Thread.
     */
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * Package-private constructor.
     */
    UseCaseThreadPoolScheduler() {
        threadPoolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(POOL_SIZE));
    }

    /**
     * Passes the given {@code runnable} to the {@link ThreadPoolExecutor} to be executed.
     */
    @Override
    public void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    /**
     * Uses the {@code handler} to send the {@link UseCase.ResponseValue} back to the UI thread.
     *
     * @param response response received from the UseCase execution.
     * @param callback callback used to communicate with the UI thread.
     */
    @Override
    public <V extends UseCase.ResponseValue> void notifyResponse(final V response, final UseCase.UseCaseCallback<V> callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response);
            }
        });
    }

    /**
     * Uses the {@code handler} to notify the UI thread that the {@code UseCase} encountered an error during execution.
     *
     * @param callback callback to the UI thread.
     */
    @Override
    public <V extends UseCase.ResponseValue> void onError(final UseCase.UseCaseCallback<V> callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError();
            }
        });
    }
}
