package com.thijsdev.studentaanhuis;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class SessionInjector implements HttpRequestInterceptor {
    private String session;
    public SessionInjector(String _session) {
        session = _session;
    }

    @Override
    public void process(HttpRequest request, HttpContext context)  throws HttpException, IOException {
        request.setHeader("Cookie", session);
    }
}