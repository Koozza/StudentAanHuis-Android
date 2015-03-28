package com.thijsdev.studentaanhuis;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

public class PrikbordActivity extends ActionBarActivity {
    public Typeface lucidaGrande, lucidaGrandeBold;
    PrikbordAdapter mAdapter = null;
    PrikbordHelper prikbordHelper = new PrikbordHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikbord);

        //Load & Set Fonts
        lucidaGrande = Typeface.createFromAsset(getAssets(), "lucida-grande.ttf");
        lucidaGrandeBold = Typeface.createFromAsset(getAssets(), "lucida-grande-bold.ttf");

        setActionBar();

        mAdapter = new PrikbordAdapter(this);

        ExpandableListView lv = (ExpandableListView) findViewById(R.id.prikbordList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Niks?
            }
        });
        lv.setAdapter(mAdapter);

        updatePrikbordItems(null);
    }

    @Override
    protected void onStop() {
        AlarmManagerHelper.getInstance().startAlarms(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        AlarmManagerHelper.getInstance().cancelAlarms(this);
        super.onStart();
    }

    private void setActionBar() {

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle(getString(R.string.prikbord));
        //mActionBar.setSubtitle(getString(R.string.prikbord));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_prikbord, menu);
        return true;
    }

    public void updatePrikbordItems(MenuItem i) {
        mAdapter.clearItems();
        prikbordHelper.updatePrikbordItems(this, new Callback() {
            @Override
            public void onTaskCompleted(Object result) {
                PrikbordItem pi = (PrikbordItem) result;
                mAdapter.addItem(pi);
            }
        }, new Callback() {
            @Override
            public void onTaskCompleted(Object result) {
                PrikbordItem pi = (PrikbordItem) result;
                mAdapter.addItem(pi);
            }
        });
    }
}