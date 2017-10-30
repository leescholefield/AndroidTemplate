package com.scholefield.lee.androidtemplate.usecase;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
@SuppressWarnings("unchecked") // mocked UseCaseCallback
public class UseCaseHandlerTest {

    private UseCaseHandler classUnderTest;

    @Before
    public void setUp() throws Exception {
        TestUseCaseScheduler scheduler = new TestUseCaseScheduler();
        classUnderTest = new UseCaseHandler(scheduler);
    }

    @Test
    public void calls_callback_onError_on_scheduler_error() throws Exception {
        UseCase.UseCaseCallback mockedCallback = mock(UseCase.UseCaseCallback.class);

        classUnderTest.execute(new TestUseCase(),
                new TestUseCase.TestRequestValues(true), mockedCallback);

        verify(mockedCallback).onError();
    }

    @Test
    public void calls_callback_onSuccess_on_successful_execution() throws Exception {
        UseCase.UseCaseCallback mockedCallback = mock(UseCase.UseCaseCallback.class);

        classUnderTest.execute(new TestUseCase(), new TestUseCase.TestRequestValues(false), mockedCallback);

        verify(mockedCallback).onSuccess(any(UseCase.ResponseValue.class));
    }

    private class TestUseCaseScheduler implements UseCaseScheduler {

        @Override
        public void execute(Runnable runnable) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(runnable);
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("executor interrupted", e);
            }
        }

        @Override
        public <V extends UseCase.ResponseValue> void notifyResponse(V response, UseCase.UseCaseCallback<V> callback) {
                callback.onSuccess(response);
        }

        @Override
        public <V extends UseCase.ResponseValue> void onError(UseCase.UseCaseCallback<V> callback) {
                callback.onError();
        }
    }

}