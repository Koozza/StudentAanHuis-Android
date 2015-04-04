package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class LoginActivity extends BasicActionBarActivity {
    Typeface robotoLight, robotoRegular, robotoMedium;
    HttpClientClass client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Load & Set Fonts
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        setFontForObject((EditText) findViewById(R.id.login_username), robotoRegular);
        setFontForObject((EditText) findViewById(R.id.login_password), robotoRegular);
        setFontForObject((Button) findViewById(R.id.login_btn_login), robotoMedium);
        setFontForObject((TextView) findViewById(R.id.login_creator), robotoLight);
        ((EditText)findViewById(R.id.login_password)).setTransformationMethod(new PasswordTransformationMethod());

        client = HttpClientClass.getInstance();
        client.init();
    }

    public void doLogin(View view) {
        final RelativeLayout loadingScreen = (RelativeLayout) this.findViewById(R.id.login_loading);
        loadingScreen.setVisibility(View.VISIBLE);

        LoginHTTPHandler lh = new LoginHTTPHandler();
        EditText username = (EditText) this.findViewById(R.id.login_username);
        EditText password = (EditText) this.findViewById(R.id.login_password);
        String uname;

        if(!username.getText().toString().contains("@"))
            uname = username.getText().toString() + "@studentaanhuis.nl";
        else
            uname = username.getText().toString();

        final Activity activity = this;

        lh.doLogin(client, this, uname, password.getText().toString(), new Callback() {
            @Override
            public void onTaskCompleted(Object result) {
                //Werkgebieden ophalen
                WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
                werkgebiedHelper.updateWerkgebieden(activity, new Callback() {
                    @Override
                    public void onTaskCompleted(Object result) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(goToNextActivity);
                        finish();
                    }
                });
            }
        }, new Callback() {
            @Override
            public void onTaskCompleted(Object result) {
                TextView error = (TextView) findViewById(R.id.login_error);
                error.setText((String) result);
                loadingScreen.setVisibility(View.GONE);
            }
        });
    }

    private void setFontForObject(TextView obj, Typeface font) {
        obj.setTypeface(font);
    }
}
