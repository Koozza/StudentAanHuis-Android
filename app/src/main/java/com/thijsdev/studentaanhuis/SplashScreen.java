package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpCookie;


public class SplashScreen extends BasicActionBarActivity {
    Typeface robotoLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //set global coockie manager
        SAHApplication.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(SAHApplication.cookieManager);

        //Zetten van de sessie
        SharedPreferences sharedpreferences = getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
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

        //Load & Set Fonts
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFontForObject((TextView) findViewById(R.id.splashscreen_creator), robotoLight);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginHTTPHandler lh = new LoginHTTPHandler();
                HttpClientClass client = HttpClientClass.getInstance();

                lh.checkLogin(SplashScreen.this, client, new Callback() {
                    @Override
                    public void onTaskCompleted(Object result) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(goToNextActivity);
                        finish();
                    }
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(Object result) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(goToNextActivity);
                        finish();
                    }
                });
            }
        }, 2000);
    }

    private void setFontForObject(TextView obj, Typeface font) {
        obj.setTypeface(font);
    }

}
