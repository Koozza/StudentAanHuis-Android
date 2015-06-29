package com.thijsdev.studentaanhuis.Prikbord;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.thijsdev.studentaanhuis.Data.DataService;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.Database.PrikbordItem;
import com.thijsdev.studentaanhuis.DividerItemDecoration;
import com.thijsdev.studentaanhuis.FragmentInterface;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;

public class PrikbordListFragment extends Fragment implements FragmentInterface {
    DatabaseHandler db;
    PrikbordHelper prikbordHelper;
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
        db = new DatabaseHandler(mainActivity);
        prikbordHelper = new PrikbordHelper(mainActivity);

        toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.prikbord));
        toolbar.inflateMenu(R.menu.menu_prikbord);
        toolbar.setNavigationIcon(null);

        registerToolbarClick();

        if(mainActivity.getSharedObject("prikbordAdapter") == null) {
            mAdapter = (PrikbordAdapter) mainActivity.addSharedObject("prikbordAdapter", new PrikbordAdapter(mainActivity));
        }else{
            mAdapter = (PrikbordAdapter) mainActivity.getSharedObject("prikbordAdapter");
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.prikbordList);
        mRecyclerView.setHasFixedSize(false);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST));

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        mainActivity.setupActionBar();

        if(mAdapter.getItemCount() == 0) {
            mAdapter.addItem(0, new PrikbordHeader(0, getString(R.string.pending), false));
            mAdapter.addItem(1, new PrikbordHeader(3, getString(R.string.no_prikbord_items_found), true));
            mAdapter.addItem(2, new PrikbordHeader(1, getString(R.string.denied), false));
            mAdapter.addItem(3, new PrikbordHeader(4, getString(R.string.no_prikbord_items_found), true));
            mAdapter.addItem(4, new PrikbordHeader(2, getString(R.string.accepted), false));
            mAdapter.addItem(5, new PrikbordHeader(5, getString(R.string.no_prikbord_items_found), true));
            loadPrikbord();
            updatePrikbordItems();
        }

        super.onStart();
    }

    public void loadPrikbord() {
        //Load prikbord items from DB
        for(PrikbordItem prikbordItem : db.getPrikbordItems()) {
            int noItemsFound = mAdapter.findItem(prikbordItem.getBeschikbaar() + 3);
            if (noItemsFound > -1)
                mAdapter.removeItem(noItemsFound);

            mAdapter.addItem(mAdapter.findItem(prikbordItem.getBeschikbaar()) + 1, prikbordItem);
        }
    }

    public void updatePrikbordItems() {
        if (!isRefreshing) {
            isRefreshing = true;
            Animation a = AnimationUtils.loadAnimation(mainActivity, R.anim.rotate);
            a.setRepeatCount(Animation.INFINITE);
            toolbar.findViewById(R.id.action_refresh).startAnimation(a);

            Intent intent = new Intent(getActivity(), DataService.class);
            intent.putExtra("ACTION", "PRIKBORD");
            getActivity().startService(intent);
        }
    }

    //Setup broadcast listener
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        PrikbordItem pi;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(DataService.PRIKBORD_ITEM_REMOVED)) {
                mAdapter.removeItem(mAdapter.findItem(intent.getIntExtra(DataService.PRIKBORD_ITEM_REMOVED, 0)));
                PrikbordHelper.fixNoItemsfound(mAdapter, getActivity());
            }

            if(intent.hasExtra(DataService.PRIKBORD_ITEM_ADDED)) {
                PrikbordItem pi = db.getPrikbordItem(intent.getIntExtra(DataService.PRIKBORD_ITEM_ADDED, 0));
                int noItemsFound = mAdapter.findItem(pi.getBeschikbaar() + 3);
                if (noItemsFound > -1)
                    mAdapter.removeItem(noItemsFound);

                mAdapter.addItem(mAdapter.findItem(pi.getBeschikbaar()) + 1, pi);
            }

            if(intent.hasExtra(DataService.PRIKBORD_ITEM_UPDATED)) {
                PrikbordItem pi = db.getPrikbordItem(intent.getIntExtra(DataService.PRIKBORD_ITEM_UPDATED, 0));
                //Remove it if already found
                if (mAdapter.hasItem(pi)) {
                    mAdapter.removeItem(mAdapter.findItem(pi.getId()));
                    PrikbordHelper.fixNoItemsfound(mAdapter, getActivity());
                }

                //Re'add it since it might have moved
                int noItemsFound = mAdapter.findItem(pi.getBeschikbaar() + 3);
                if (noItemsFound > -1)
                    mAdapter.removeItem(noItemsFound);

                mAdapter.addItem(mAdapter.findItem(pi.getBeschikbaar()) + 1, pi);
            }

            if(intent.hasExtra(DataService.PRIKBORD_FINISHED)) {
                if (toolbar.findViewById(R.id.action_refresh) != null)
                    toolbar.findViewById(R.id.action_refresh).clearAnimation();
                isRefreshing = false;
            }
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("DATA_UPDATE"));

        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void unload() {
        toolbar.findViewById(R.id.action_refresh).clearAnimation();
        toolbar.removeView(toolbar.findViewById(R.id.action_refresh));
    }

    public void registerToolbarClick() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_refresh:
                        updatePrikbordItems();
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public int getDrawerId() {
        return R.id.menu_prikbord;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.prikbord);
    }
}
