package com.thijsdev.studentaanhuis;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PrikbordDetailFragment extends Fragment {
    private WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
    private GeoLocationHelper locHelper = new GeoLocationHelper();
    DatabaseHandler databaseHandler;
    PrikbordItem prikbordItem;

    private String locationId, omschrijvingId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prikbord_detail, container, false);
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


        prikbordLocatie.setTypeface(((PrikbordActivity)getActivity()).robotoMedium);
        prikbordDistance.setTypeface(((PrikbordActivity)getActivity()).robotoRegular);
        prikbordDescription.setTypeface(((PrikbordActivity) getActivity()).robotoRegular);

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
