package com.thijsdev.studentaanhuis;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class PrikbordActivity extends BasicActionBarActivity {
    public Typeface robotoLight, robotoRegular, robotoMedium;
    PrikbordHelper prikbordHelper = new PrikbordHelper();
    Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private PrikbordAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");


        mRecyclerView = (RecyclerView) findViewById(R.id.prikbordList);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PrikbordAdapter(this);
        mRecyclerView.setAdapter(mAdapter);


        updatePrikbordItems(null);
    }

    private int GetDipsFromPixel(int pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
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

            //TODO: mAdapter.clearItems();
            prikbordHelper.updatePrikbordItems(this, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    PrikbordItem pi = (PrikbordItem) result;
                    if(!mAdapter.hasItem(pi))
                        mAdapter.addItem(0, pi);
                }
            }, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    PrikbordItem pi = (PrikbordItem) result;
                    if(!mAdapter.hasItem(pi))
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
}