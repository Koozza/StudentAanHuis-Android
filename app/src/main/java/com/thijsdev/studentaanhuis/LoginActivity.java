package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;


public class LoginActivity extends Activity {
    HttpClientClass client;
    String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        client = HttpClientClass.getInstance();
        client.init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doLogin(View view) {
        //getUsername();
        LoginHTTPHandler lh = new LoginHTTPHandler();
        EditText username = (EditText) this.findViewById(R.id.login_username);
        EditText password = (EditText) this.findViewById(R.id.login_password);

        lh.doLogin(client, this, username.getText().toString(), password.getText().toString());
    }
}
