package com.thijsdev.studentaanhuis.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.BasicActionBarActivity;
import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;
import com.thijsdev.studentaanhuis.SAHApplication;
import com.thijsdev.studentaanhuis.Werkgebied.WerkgebiedHelper;

import java.net.HttpCookie;


public class LoginActivity extends BasicActionBarActivity {
    Typeface robotoLight, robotoRegular, robotoMedium;

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

        lh.doLogin(this, uname, password.getText().toString(), new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                //Zetten van de juiste cookie
                for(HttpCookie cookie : SAHApplication.cookieManager.getCookieStore().getCookies()) {
                    if(cookie.getName().equals("_session_id")) {
                        SharedPreferences sharedpreferences = getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);

                        SharedPreferences.Editor edit = sharedpreferences.edit();
                        edit.putString("session", cookie.getValue());
                        edit.commit();
                    }
                }

                //Werkgebieden ophalen
                WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
                werkgebiedHelper.updateWerkgebieden(activity, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {

                        //Checken of er een werkgebied is, en deze als default zetten
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String werkgebiedID = sharedPref.getString("prikbord_werkgebied", "");

                        if(werkgebiedID == "" || werkgebiedID == null) {
                            SharedPreferences.Editor edit = sharedPref.edit();
                            WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
                            edit.putString("prikbord_werkgebied", Integer.toString(werkgebiedHelper.getActiveWerkgebieden(getApplicationContext()).get(0).getId()));
                            edit.commit();
                        }

                        Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(goToNextActivity);
                        finish();
                    }
                });
            }
        }, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                TextView error = (TextView) findViewById(R.id.login_error);
                error.setText((String) results[0]);
                loadingScreen.setVisibility(View.GONE);
            }
        });
    }

    private void setFontForObject(TextView obj, Typeface font) {
        obj.setTypeface(font);
    }
}
