package com.thijsdev.studentaanhuis;

import android.app.Application;

import java.net.CookieManager;

//public class SAHApplication extends android.support.multidex.MultiDexApplication {
public class SAHApplication extends Application {
    final public static int HTTP_RETRIES = 3;
    final public static int HTTP_DELAY = 200;
    final public static int HTTP_RATE_LIMIT_TIMEOUT = 3000;

    public static HttpClientClass httpClientClass = new HttpClientClass();

    public static CookieManager cookieManager = new CookieManager();

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static boolean isTimerRunning() {
        return timerRunning;
    }

    public static void stopTimer() {
        timerRunning = false;
    }

    public static void startTimer() {
        timerRunning = true;
    }

    private static boolean activityVisible = false;
    private static boolean timerRunning = true;
}
