package com.thijsdev.studentaanhuis;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

public class PrikbordActivity extends ActionBarActivity {
    PrikbordAdapter mAdapter = null;
    PrikbordHelper prikbordHelper = new PrikbordHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikbord);
        setActionBar();

        mAdapter = new PrikbordAdapter(this);

        prikbordHelper.updatePrikbordItems(this, mAdapter);

        ExpandableListView lv = (ExpandableListView) findViewById(R.id.prikbordList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Niks?
            }
        });
        lv.setAdapter(mAdapter);
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

    public void reloadPrikbordItems(MenuItem  item) {
        mAdapter.clearItems();
        prikbordHelper.updatePrikbordItems(this, mAdapter);
    }
}