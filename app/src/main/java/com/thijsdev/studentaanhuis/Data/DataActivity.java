package com.thijsdev.studentaanhuis.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.BasicActionBarActivity;
import com.thijsdev.studentaanhuis.R;

public class DataActivity extends BasicActionBarActivity {
    Typeface robotoLight, robotoRegular;
    private DataService s;
    private ProgressBar progressBar;
    ProgressBar p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //Load & Set Fonts
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        setFontForObject((TextView) findViewById(R.id.data_firsttime), robotoRegular);
        setFontForObject((TextView) findViewById(R.id.data_state), robotoLight);

        progressBar = (ProgressBar) findViewById(R.id.data_progressbar);

        Intent intent = new Intent(this, DataService.class);
        intent.putExtra("ACTION", "ALL");
        startService(intent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(progressBar != null) {
                if(intent.hasExtra(DataService.CURRENTLY_UPDATING))
                    ((TextView) findViewById(R.id.data_state)).setText(intent.getStringExtra(DataService.CURRENTLY_UPDATING));

                if(intent.hasExtra(DataService.CLEAR_PROGRESS))
                    ((ProgressBar) findViewById(R.id.data_progressbar)).setProgress(0);

                if(intent.hasExtra(DataService.INCREASE_PROGRESS))
                    ((ProgressBar) findViewById(R.id.data_progressbar)).incrementProgressBy(1);

                if(intent.hasExtra(DataService.SET_TOTAL_PROGRESS))
                    ((ProgressBar) findViewById(R.id.data_progressbar)).setMax(intent.getIntExtra(DataService.SET_TOTAL_PROGRESS, 0));
            }
        }
    };
    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("DATA_UPDATE"));

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    private void setFontForObject(TextView obj, Typeface font) {
        obj.setTypeface(font);
    }
}
