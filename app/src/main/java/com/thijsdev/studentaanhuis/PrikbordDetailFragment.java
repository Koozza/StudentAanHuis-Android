package com.thijsdev.studentaanhuis;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PrikbordDetailFragment extends Fragment {
    private WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
    private GeoLocationHelper locHelper = new GeoLocationHelper();

    private MainActivity mainActivity;
    private Toolbar toolbar;

    DatabaseHandler databaseHandler;
    PrikbordItem prikbordItem;

    private String locationId, omschrijvingId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prikbord_detail, container, false);

        mainActivity = (MainActivity) view.getContext();
        toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.prikbord_item));
        toolbar.inflateMenu(R.menu.menu_prikbord_detail);
        mainActivity.mDrawerToggle.setDrawerIndicatorEnabled(false);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mainActivity.mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });

        /*
        if(android.os.Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.prikbord_locatie).setTransitionName(locationId);
            view.findViewById(R.id.prikbord_omschrijving).setTransitionName(locationId);
        }
        */

        databaseHandler = new DatabaseHandler(getActivity());

        Bundle bundle = this.getArguments();
        int PrikbordId = bundle.getInt("PrikbordId", -1);
        prikbordItem = databaseHandler.getPrikbordItem(PrikbordId);
        String distance = getDistanceString(prikbordItem);

        TextView prikbordLocatie = (TextView) view.findViewById(R.id.prikbord_locatie);
        TextView prikbordDistance = (TextView) view.findViewById(R.id.prikbord_afstand);
        TextView prikbordDescription = (TextView) view.findViewById(R.id.prikbord_omschrijving);

        prikbordLocatie.setText(prikbordItem.getAdres());
        prikbordDescription.setText(prikbordItem.getBeschrijving());
        if(distance == null)
            prikbordDistance.setVisibility(View.GONE);
        else
            prikbordDistance.setText(distance);


        prikbordLocatie.setTypeface(((MainActivity)getActivity()).robotoMedium);
        prikbordDistance.setTypeface(((MainActivity)getActivity()).robotoRegular);
        prikbordDescription.setTypeface(((MainActivity) getActivity()).robotoRegular);

        return view;
    }

    private String getDistanceString(PrikbordItem prikbordItem) {
        Location werkgebiedLocation = werkgebiedHelper.getFirstWerkgebiedLocation(getActivity());

        if(werkgebiedLocation != null && prikbordItem.getLocation() != null) {
            if((werkgebiedLocation.getLatitude() == 0 && werkgebiedLocation.getLongitude() == 0) || (prikbordItem.getLocation().getLatitude() == 0 && prikbordItem.getLocation().getLongitude() == 0)) {
                return null;
            }else {

                int distance = locHelper.getDistanceBetweenLocations(werkgebiedLocation, prikbordItem.getLocation());
                if (distance < 1000) {
                    return Integer.toString(distance) + "M";
                } else {
                    return Float.toString((float) ((int) (distance / 100)) / 10f) + "Km";
                }
            }
        }else{
            return null;
        }
    }

    /*
    public void setLocationId(String id) {
        locationId = id;
    }

    public void setOmschrijvingId(String id) {
        omschrijvingId = id;
    }
    */
}
