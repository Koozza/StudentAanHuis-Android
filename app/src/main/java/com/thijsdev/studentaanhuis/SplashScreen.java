package com.thijsdev.studentaanhuis;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


public class SplashScreen extends BasicActionBarActivity {
    Typeface robotoLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Load & Set Fonts
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFontForObject((TextView) findViewById(R.id.splashscreen_creator), robotoLight);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginHTTPHandler lh = new LoginHTTPHandler();
                HttpClientClass client = HttpClientClass.getInstance();
                client.init();
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
