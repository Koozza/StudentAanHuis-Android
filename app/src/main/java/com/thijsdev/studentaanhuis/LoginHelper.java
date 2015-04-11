package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class LoginHelper {
    private DialogInterface.OnClickListener dialogClickListener;

    public void doLogout(Activity activity, boolean askQuestion) {
        //Ask question
        if(askQuestion == true) {
            generateDoLogoutQuestion(activity);
        } else {

            //Shared preff unsetten
            SharedPreferences sharedpreferences = activity.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);

            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.remove("session");
            edit.commit();

            //Cookie uit de Cookiemanger halen
            SAHApplication.cookieManager.getCookieStore().removeAll();

            //Activity stoppen en login launchen
            Intent goToNextActivity = new Intent(activity.getApplicationContext(), LoginActivity.class);
            activity.startActivity(goToNextActivity);
            activity.finish();
        }
    }

    private void generateDoLogoutQuestion(final Activity activity) {
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        doLogout(activity, false);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.sure_you_want_to_logout)).setPositiveButton(activity.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(activity.getString(R.string.no), dialogClickListener).show();
    }
}
