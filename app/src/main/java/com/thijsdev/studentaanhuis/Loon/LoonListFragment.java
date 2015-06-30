package com.thijsdev.studentaanhuis.Loon;

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
import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.DividerItemDecoration;
import com.thijsdev.studentaanhuis.FragmentInterface;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;

import java.util.Date;
import java.util.TreeMap;

public class LoonListFragment extends Fragment implements FragmentInterface {
    DatabaseHandler db;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mainActivity;
    private Toolbar toolbar;

    public LoonAdapter mAdapter;

    private boolean isRefreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loon_list, container, false);

        mainActivity = (MainActivity) view.getContext();
        db = new DatabaseHandler(mainActivity);
        toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.loon));
        toolbar.inflateMenu(R.menu.menu_loon);
        toolbar.setNavigationIcon(null);

        registerToolbarClick();

        if(mainActivity.getSharedObject("loonAdapter") == null) {
            mAdapter = (LoonAdapter) mainActivity.addSharedObject("loonAdapter", new LoonAdapter(mainActivity));
        }else{
            mAdapter = (LoonAdapter) mainActivity.getSharedObject("loonAdapter");
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.loonList);
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
            loadLoon();
            updateLoon();
        }

        super.onStart();
    }

    public void loadLoon() {
        //Load prikbord items from DB
        TreeMap<Date, LoonMaand> loonMaandHashMap = new TreeMap<>();
        for(LoonMaand loonMaand : db.getLoonMaanden()) {
            loonMaandHashMap.put(loonMaand.getDatum(), loonMaand);
        }

        //Add them to the listview
        for(LoonMaand loonMaand : loonMaandHashMap.values()) {
            mAdapter.addItem(0, loonMaand);
        }
    }

    public void updateLoon() {
        if (!isRefreshing) {
            isRefreshing = true;
            Animation a = AnimationUtils.loadAnimation(mainActivity, R.anim.rotate);
            a.setRepeatCount(Animation.INFINITE);
            toolbar.findViewById(R.id.action_refresh).startAnimation(a);

            Intent intent = new Intent(getActivity(), DataService.class);
            intent.putExtra("ACTION", "LOON");
            getActivity().startService(intent);
        }

        /*
        if (!isRefreshing) {
            isRefreshing = true;
            Animation a = AnimationUtils.loadAnimation(mainActivity, R.anim.rotate);
            a.setRepeatCount(Animation.INFINITE);
            toolbar.findViewById(R.id.action_refresh).startAnimation(a);

            //TODO: mAdapter.clearItems();
            LoonHelper loonHelper = new LoonHelper(mainActivity);
            loonHelper.updateLoon(mainActivity, new Callback() {
                @Override
                public void onTaskCompleted(Object... results) {
                    TreeMap<Date, LoonMaand> loonMaandHashMap = (TreeMap<Date, LoonMaand>) results[0];
                    for (LoonMaand loonMaand : loonMaandHashMap.values()) {
                        if(mAdapter.getPostition(loonMaand) == -1)
                            mAdapter.addItem(0, loonMaand);
                        else
                            mAdapter.updateItem(loonMaand);
                    }

                    //Stop refresh animation
                    if (toolbar.findViewById(R.id.action_refresh) != null)
                        toolbar.findViewById(R.id.action_refresh).clearAnimation();
                    isRefreshing = false;
                }
            });
        }
        */
    }

    //Setup broadcast listener
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(DataService.LOON_ITEM_ADDED)) {
                mAdapter.addItem(0, db.getLoonMaand(intent.getStringExtra(DataService.LOON_ITEM_UPDATED)));
            }

            if(intent.hasExtra(DataService.LOON_ITEM_UPDATED)) {
                mAdapter.updateItem(db.getLoonMaand(intent.getStringExtra(DataService.LOON_ITEM_UPDATED)));
            }

            if(intent.hasExtra(DataService.LOON_FINISHED)) {
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
                        updateLoon();
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public int getDrawerId() {
        return R.id.menu_loon;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.loon);
    }
}
