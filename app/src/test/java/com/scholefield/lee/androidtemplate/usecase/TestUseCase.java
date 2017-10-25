package com.scholefield.lee.androidtemplate.usecase;

/**
 *
 */
public class TestUseCase extends UseCase<TestUseCase.TestRequestValues, TestUseCase.TestResponseValue> {

    @Override
    protected void executeUseCase(TestRequestValues requestValues) {
        boolean throwException = requestValues.shouldThrowException;
        if (throwException) {
            getUseCaseCallback().onError();
        } else {
            getUseCaseCallback().onSuccess(new TestResponseValue("new value"));
        }
    }

    public static class TestRequestValues implements UseCase.RequestValues {

        private boolean shouldThrowException;

        public TestRequestValues(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }

    }

    public static class TestResponseValue implements UseCase.ResponseValue {
        String value;

        public TestResponseValue(String value) {
            this.value = value;
        }

        public String getResponseValue(){return value;}
    }

}
