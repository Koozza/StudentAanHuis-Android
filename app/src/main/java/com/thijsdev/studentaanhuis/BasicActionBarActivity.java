package com.thijsdev.studentaanhuis;

import android.support.v7.app.ActionBarActivity;

public class BasicActionBarActivity extends ActionBarActivity {
    @Override
    protected void onResume() {
        super.onResume();

        if(SAHApplication.isTimerRunning()) {
            SAHApplication.stopTimer();
            AlarmManagerHelper.getInstance().cancelAlarms(this);
        }

        SAHApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SAHApplication.activityPaused();
    }

    @Override
    protected void onStop() {
        if(!SAHApplication.isActivityVisible()) {
            SAHApplication.startTimer();
            AlarmManagerHelper.getInstance().startAlarms(this);
        }
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        if(getFragmentManager().getBackStackEntryCount() == 0)
            super.onBackPressed();
        else
            getFragmentManager().popBackStack();
    }
}
