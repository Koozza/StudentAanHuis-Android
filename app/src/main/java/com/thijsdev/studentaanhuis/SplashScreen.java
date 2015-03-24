package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginHTTPHandler lh = new LoginHTTPHandler();
                HttpClientClass client = HttpClientClass.getInstance();
                client.init();
                lh.checkLogin(SplashScreen.this, client, new Callback() {
                    @Override
                    public void onTaskCompleted(String result) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), PrikbordActivity.class);
                        startActivity(goToNextActivity);
                        finish();
                    }
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(String result) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(goToNextActivity);
                        finish();
                    }
                });
            }
        }, 2000);
    }

}
