package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpCookie;

public class SessionHelper{
    public static void registerCookieHandler() {
        //set global coockie manager
        SAHApplication.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(SAHApplication.cookieManager);
    }

    public static void restoreSession(Context context) {
        //Zetten van de sessie
        SharedPreferences sharedpreferences = context.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        String session = sharedpreferences.getString("session", null);
        if(session != null) {
            HttpCookie cookie = new HttpCookie("_session_id", session);
            cookie.setDomain("nl.sah3.net");
            cookie.setPath("/");
            cookie.setMaxAge(-1);
            cookie.setSecure(true);
            cookie.setVersion(0);

            try {
                Field fieldHttpOnly = cookie.getClass().getDeclaredField("httpOnly");
                fieldHttpOnly.setAccessible(true);
                fieldHttpOnly.set(cookie, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SAHApplication.cookieManager.getCookieStore().add(null, cookie);
        }
    }
}
