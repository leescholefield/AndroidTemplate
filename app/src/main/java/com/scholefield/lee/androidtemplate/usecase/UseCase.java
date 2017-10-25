package com.scholefield.lee.androidtemplate.usecase;

/**
 * The base class for a new {@code UseCase}. When instantiating a new UseCase the caller must set the {@link RequestValues}
 * via {@link #setRequestValues}. If the caller expects a response it must also set a {@link UseCaseCallback} via
 * {@link #setUseCaseCallback}.
 *
 * A Use Case acts as an interface between the domain layer and the application. They are used to define all of the operations that
 * the presenter can perform.
 *
 * Since the execution of a {@code UseCase} will be handled by a separate thread it is best to avoid relying on mutable objects
 * or un-thread-safe classes.
 */
public abstract class UseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValue> {

    private Q requestValues;

    private UseCaseCallback<P> useCaseCallback;

    public void setRequestValues(Q requestValues) {
        this.requestValues = requestValues;
    }

    public Q getRequestValues() {
        return requestValues;
    }

    public UseCaseCallback<P> getUseCaseCallback() {
        return useCaseCallback;
    }

    /**
     * @param callback callback used to communicate with calling thread.
     */
    public void setUseCaseCallback(UseCaseCallback<P> callback) {
        this.useCaseCallback = callback;
    }

    /**
     * Executes the {@code UseCase}.
     */
    public final void run() {
        executeUseCase(requestValues);
    }

    /**
     * Subclasses must override this to provide implementation details.
     */
    protected abstract void executeUseCase(Q requestValues);

    /**
     * Used to wrap additional options for the returned {@code ResponseValue}. For example, you could pass a Utility class
     * that defines a method to order a list of values returned from the model.
     */
    public interface RequestValues {
    }

    /**
     * Wrapper around the data retrieved from the model.
     */
    public interface ResponseValue {
    }

    /**
     * Used to communicate the {@code ResponseValue} back to the caller.
     * @param <R> ResponseValue
     */
    public interface UseCaseCallback<R> {
        void onSuccess(R response);
        void onError();
    }
}