package com.thijsdev.studentaanhuis;

import org.json.JSONObject;

public class HttpClientObject {
    final public static int GET = 1;
    final public static int POST = 2;

    private JSONObject arguments;
    private Callback success;
    private Callback failed;
    private int attempt;
    private int type = GET;


    public JSONObject getArguments() {
        return arguments;
    }

    public void setArguments(JSONObject arguments) {
        this.arguments = arguments;
    }

    public Callback getSuccess() {
        return success;
    }

    public void setSuccess(Callback success) {
        this.success = success;
    }

    public Callback getFailed() {
        return failed;
    }

    public void setFailed(Callback failed) {
        this.failed = failed;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public void addAttempt() {
        this.attempt = this.attempt + 1;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
