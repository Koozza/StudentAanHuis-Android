package com.thijsdev.studentaanhuis;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

public class PrikbordActivity extends ActionBarActivity {
    public Typeface lucidaGrande, lucidaGrandeBold;
    PrikbordAdapter mAdapter = null;
    PrikbordHelper prikbordHelper = new PrikbordHelper();
    Toolbar toolbar;

    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikbord);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.prikbord));
        toolbar.inflateMenu(R.menu.menu_prikbord);
        registerToolbarClick();

        //Load & Set Fonts
        lucidaGrande = Typeface.createFromAsset(getAssets(), "lucida-grande.ttf");
        lucidaGrandeBold = Typeface.createFromAsset(getAssets(), "lucida-grande-bold.ttf");


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
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_prikbord, menu);
        return true;
    }

    public void registerToolbarClick() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_refresh:
                        updatePrikbordItems(item);
                        return true;
                }

                return false;
            }
        });
    }

    public void updatePrikbordItems(MenuItem i) {
        if(!isRefreshing) {
            isRefreshing = true;
            Animation a = AnimationUtils.loadAnimation(getApplication(), R.anim.rotate);
            a.setRepeatCount(Animation.INFINITE);
            toolbar.findViewById(R.id.action_refresh).startAnimation(a);

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
            }, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    toolbar.findViewById(R.id.action_refresh).clearAnimation();
                    isRefreshing = false;
                }
            });
        }
    }
}