package com.thijsdev.studentaanhuis;

import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class PrikbordActivity extends BasicActionBarActivity {
    public Typeface robotoLight, robotoRegular, robotoMedium;
    PrikbordHelper prikbordHelper = new PrikbordHelper();
    Toolbar toolbar;
    PrikbordListFragment prikbordListFragment;
    boolean inSettings = true;
    public PrikbordAdapter mAdapter;

    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikbord);

        mAdapter = new PrikbordAdapter(this);

        if (findViewById(R.id.prikbord_fragments) != null) {
            if (savedInstanceState != null)
                return;

            prikbordListFragment = new PrikbordListFragment();
            prikbordListFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.prikbord_fragments, prikbordListFragment);
            transaction.commit();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.prikbord));
        toolbar.inflateMenu(R.menu.menu_prikbord);
        registerToolbarClick();

        //Load & Set Fonts
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");


        updatePrikbordItems(null);
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
        if (!isRefreshing) {
            isRefreshing = true;
            Animation a = AnimationUtils.loadAnimation(getApplication(), R.anim.rotate);
            a.setRepeatCount(Animation.INFINITE);
            toolbar.findViewById(R.id.action_refresh).startAnimation(a);

            //TODO: mAdapter.clearItems();
            prikbordHelper.updatePrikbordItems(this, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    PrikbordItem pi = (PrikbordItem) result;
                    if (!mAdapter.hasItem(pi))
                        mAdapter.addItem(0, pi);
                }
            }, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    PrikbordItem pi = (PrikbordItem) result;
                    if (!mAdapter.hasItem(pi))
                        mAdapter.addItem(0, pi);
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

    @Override
    public void onBackPressed()
    {
        if(getFragmentManager().getBackStackEntryCount() == 0)
            super.onBackPressed();
        else
            getFragmentManager().popBackStack();
    }
}