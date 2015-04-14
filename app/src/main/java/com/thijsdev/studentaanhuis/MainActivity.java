package com.thijsdev.studentaanhuis;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BasicActionBarActivity {
    public Typeface robotoLight, robotoRegular, robotoMedium, robotoBold;

    public LoginHelper loginHelper = new LoginHelper();

    private Toolbar toolbar;
    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;

    private Map<String, Object> sharedObjects = new HashMap<>();

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikbord);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (findViewById(R.id.prikbord_fragments) != null) {
            if (savedInstanceState != null)
                return;

            currentFragment = new PrikbordListFragment();
            currentFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.prikbord_fragments, currentFragment);
            transaction.commit();
        }

        //toolbar.setNavigationIcon(R.drawable.icon);
        //toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        //toolbar.setLogo(R.drawable.ic_launcher);

        //Load & Set Fonts
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        robotoBold = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
    }

    public void setupActionBar() {
        if(mDrawerToggle == null)
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    public void menuClick(View v) {
        if(v.getTag().equals("logout")) {
            loginHelper.doLogout(this, true);
        }else if(v.getTag().equals("settings")) {
            switchFragment(new PreferencesFragment());
        }else if(v.getTag().equals("prikbord")) {
            switchFragment(new PrikbordListFragment());
        }
        mDrawerLayout.closeDrawers();
    }

    public void switchFragment(Fragment fragment) {
        Fragment menu = getFragmentManager().findFragmentById(R.id.fragment_drawer);
        menu.getView().findViewById(((FragmentInterface) currentFragment).getDrawerId()).setBackgroundColor(getResources().getColor(R.color.white));
        menu.getView().findViewById(((FragmentInterface) fragment).getDrawerId()).setBackgroundColor(getResources().getColor(R.color.SAHlightblue));

        currentFragment = fragment;

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.prikbord_fragments, fragment);
        transaction.commit();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public Object addSharedObject(String str, Object obj) {
        sharedObjects.put(str, obj);
        return obj;
    }

    public Object getSharedObject(String str) {
        return sharedObjects.get(str);
    }

    public void removeSharedObject(String str) {
        sharedObjects.remove(str);
    }

    public Fragment getActiveFragement() {
        return currentFragment;
    }
}