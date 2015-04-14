package com.thijsdev.studentaanhuis;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

public class PreferencesFragment extends PreferenceFragment implements FragmentInterface {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        MainActivity mainActivity = (MainActivity) getActivity();
        Toolbar toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.settings));
    }

    @Override
    public int getDrawerId() {
        return R.id.menu_settings;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.settings);
    }
}
