package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.widget.Toast;

public class DefaultCallbackFailure extends Callback {
    private Context _context;
    private Callback _failure;

    public DefaultCallbackFailure(Context context) {
        _context = context;
        _failure = new Callback();
    }

    public DefaultCallbackFailure(Context context, Callback failure) {
        _context = context;
        _failure = failure;
    }

    @Override
    public void onTaskCompleted(Object... results) {
        HttpClientClass client = ((HttpClientClass)results[1]);
        if(client.getHttpClientObject().getAttempt() < SAHApplication.HTTP_RETRIES) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.retryLastCall();
        }else {
            _failure.onTaskCompleted((Object[])null);

            Toast toast = Toast.makeText(_context, _context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
