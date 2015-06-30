package com.thijsdev.studentaanhuis;

public class RetryCallbackFailure extends Callback {
    private Callback _failure;
    private int _retryCount;

    public RetryCallbackFailure(int retryCount) {
        _retryCount = retryCount;
        _failure = new Callback();
    }

    public RetryCallbackFailure(Callback failure, int retryCount) {
        _failure = failure;
        _retryCount = retryCount;
    }

    @Override
    public void onTaskCompleted(Object... results) {
        HttpClientClass client = ((HttpClientClass)results[1]);
        if(client.getHttpClientObject().getAttempt() < _retryCount) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.retryLastCall();
        }else {
            _failure.onTaskCompleted(results);
        }
    }
}
