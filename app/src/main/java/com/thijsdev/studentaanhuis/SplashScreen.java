package com.thijsdev.studentaanhuis;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.Login.LoginActivity;
import com.thijsdev.studentaanhuis.Login.LoginHTTPHandler;


public class SplashScreen extends BasicActionBarActivity {
    Typeface robotoLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //Restore session
        SessionHelper.registerCookieHandler();
        SessionHelper.restoreSession(this);

        //Load & Set Fonts
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFontForObject((TextView) findViewById(R.id.splashscreen_creator), robotoLight);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginHTTPHandler lh = new LoginHTTPHandler();

                lh.checkLogin(SplashScreen.this, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(goToNextActivity);
                        finish();
                    }
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
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
