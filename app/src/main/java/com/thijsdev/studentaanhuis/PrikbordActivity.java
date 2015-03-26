package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

public class PrikbordActivity extends Activity {
    PrikbordAdapter mAdapter = null;
    PrikbordHelper prikbordHelper = new PrikbordHelper();
    WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prikbord);

        mAdapter = new PrikbordAdapter(this);

        prikbordHelper.updatePrikbordItems(this, mAdapter);

        ExpandableListView lv = (ExpandableListView) findViewById(R.id.prikbordList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO: Deze click moet natuurlijk de info door gaan geven naar de volgende activity.
                //TODO: Click reageert ook op separators! Let hiermee op, dat mag natuurlijk niks doen
            }
        });
        lv.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_prikbord, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_force_reload_prikbord) {
            prikbordHelper.forceUpdatePrikbordItems(this, mAdapter);
            return true;
        }

        if(id == R.id.action_force_reload_werkgebied) {
            final RelativeLayout loadingScreen = (RelativeLayout) this.findViewById(R.id.prikbord_loading);
            loadingScreen.setVisibility(View.VISIBLE);
            werkgebiedHelper.forceUpdateWerkgebieden(this, new Callback() {
                @Override
                public void onTaskCompleted(String result) {
                    loadingScreen.setVisibility(View.GONE);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}