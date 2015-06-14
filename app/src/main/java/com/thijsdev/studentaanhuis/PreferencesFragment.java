package com.thijsdev.studentaanhuis;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import com.thijsdev.studentaanhuis.Werkgebied.WerkgebiedHelper;

public class PreferencesFragment extends PreferenceFragment implements FragmentInterface {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        MainActivity mainActivity = (MainActivity) getActivity();
        Toolbar toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.settings));

        //Setup werkgebied selector
        WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper(mainActivity);
        createListEntry("prikbord_werkgebied", werkgebiedHelper.getWerkgebiedenArray(getActivity()), werkgebiedHelper.getWerkgebiedenIDArray(getActivity()), (String) werkgebiedHelper.getWerkgebiedenIDArray(getActivity())[0]);
    }

    private void createListEntry(String name, final CharSequence[] entries, final CharSequence[] entryValues, final String defaultValue) {
        final ListPreference listPreference = (ListPreference) findPreference(name);
        setValues(listPreference, entries, entryValues, defaultValue);
        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                setValues(listPreference, entries, entryValues, defaultValue);
                return false;
            }
        });
    }

    private void setValues(ListPreference lp, CharSequence[] entries, CharSequence[] entryValues, String defaultValue) {
        lp.setEntries(entries);
        lp.setDefaultValue(defaultValue);
        lp.setEntryValues(entryValues);
    }

    @Override
    public int getDrawerId() {
        return R.id.menu_settings;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.settings);
    }

    @Override
    public void unload() { }
}
