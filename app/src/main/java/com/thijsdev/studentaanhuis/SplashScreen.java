package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.Data.DataActivity;
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
                        SharedPreferences sharedpreferences = getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
                        Intent goToNextActivity;

                        if(sharedpreferences.getInt("DATA_VERSION", -1) == DataActivity.VERSION) {
                            goToNextActivity = new Intent(getApplicationContext(), DataActivity.class);
                        }else{
                            goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                        }

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
        }, 1500);
    }

    private void setFontForObject(TextView obj, Typeface font) {
        obj.setTypeface(font);
    }

}
