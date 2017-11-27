package com.scholefield.lee.androidtemplate.usecase;

/**
 * This class is responsible for executing a {@link UseCase}.
 */
public interface UseCaseHandler {

    <V extends UseCase.RequestValues, R extends UseCase.ResponseValue> void execute(
            final UseCase<V, R> useCase, V values, UseCase.UseCaseCallback<R> callback);
}
