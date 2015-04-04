package com.thijsdev.studentaanhuis;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class PrikbordListFragment extends Fragment {
    private PrikbordHelper prikbordHelper = new PrikbordHelper();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mainActivity;
    private Toolbar toolbar;

    public PrikbordAdapter mAdapter;

    private boolean isRefreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prikbord_list, container, false);

        mainActivity = (MainActivity) view.getContext();
        toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.prikbord));
        toolbar.inflateMenu(R.menu.menu_prikbord);
        toolbar.setNavigationIcon(null);

        mainActivity.mDrawerToggle.setDrawerIndicatorEnabled(true);
        mainActivity.mDrawerLayout.setDrawerListener(mainActivity.mDrawerToggle);
        mainActivity.mDrawerToggle.syncState();

        registerToolbarClick();

        if(mainActivity.getSharedObject("prikbordAdapter") == null) {
            mAdapter = (PrikbordAdapter) mainActivity.addSharedObject("prikbordAdapter", new PrikbordAdapter(mainActivity));
        }else{
            mAdapter = (PrikbordAdapter) mainActivity.getSharedObject("prikbordAdapter");
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.prikbordList);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST));

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        if(mAdapter.getItemCount() == 0)
            updatePrikbordItems(null);
        super.onStart();
    }

    public void updatePrikbordItems(MenuItem i) {
        if (!isRefreshing) {
            isRefreshing = true;
            Animation a = AnimationUtils.loadAnimation(mainActivity, R.anim.rotate);
            a.setRepeatCount(Animation.INFINITE);
            toolbar.findViewById(R.id.action_refresh).startAnimation(a);

            //TODO: mAdapter.clearItems();
            prikbordHelper.updatePrikbordItems(mainActivity, new Callback() {
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
}
