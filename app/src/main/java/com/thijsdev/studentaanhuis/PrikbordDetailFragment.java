package com.thijsdev.studentaanhuis;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        //Set labels as bold
        ((TextView) view.findViewById(R.id.prikbord_label_afstand)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_deadline)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_locatie)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_status)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_tags)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_omschrijving)).setTypeface(((MainActivity) getActivity()).robotoMedium);

        TextView prikbordLocatie = (TextView) view.findViewById(R.id.prikbord_locatie);
        TextView prikbordDistance = (TextView) view.findViewById(R.id.prikbord_afstand);
        TextView prikbordStatus = (TextView) view.findViewById(R.id.prikbord_status);
        TextView prikbordDeadline = (TextView) view.findViewById(R.id.prikbord_deadline);
        TextView prikbordTags = (TextView) view.findViewById(R.id.prikbord_tags);
        TextView prikbordDescription = (TextView) view.findViewById(R.id.prikbord_description);

        prikbordDescription.setText(prikbordItem.getBeschrijving());
        prikbordTags.setText(prikbordItem.getType());

        //Fix adress to strip postcode
        Pattern p = Pattern.compile("(\\w+), \\d+ \\w+\\s+(\\w+)");
        Matcher m = p.matcher(prikbordItem.getAdres());
        m.find();
        prikbordLocatie.setText(m.group(1)+", "+m.group(2));


        //set deadline
        prikbordDeadline.setText(prikbordItem.getFormatedDeadline("EEE dd MMMM yyyy", null));

        //set distance
        if(distance == null)
            prikbordDistance.setVisibility(View.GONE);
        else
            prikbordDistance.setText(distance);

        //set status
        if(prikbordItem.getBeschikbaar() == 0)
            prikbordStatus.setText(mainActivity.getString(R.string.none));
        else if(prikbordItem.getBeschikbaar() == 1)
            prikbordStatus.setText(mainActivity.getString(R.string.unavailable));
        else
            prikbordStatus.setText( mainActivity.getString(R.string.available));


        prikbordLocatie.setTypeface(((MainActivity)getActivity()).robotoRegular);
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
}
