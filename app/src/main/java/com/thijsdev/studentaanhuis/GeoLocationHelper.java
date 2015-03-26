package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;

public class GeoLocationHelper {
    public Location getLocationFromAddress(Context context, String Adress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        Location location = null;

        try {
            address = coder.getFromLocationName(Adress,5);
            if (address == null) {
                return null;
            }
            Address adressLocation = address.get(0);

            location = new Location("");
            location.setLatitude(adressLocation.getLatitude());
            location.setLongitude(adressLocation.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return location;
    }

    public int getDistanceBetweenLocations(Location loc1, Location loc2) {
        float distance = loc1.distanceTo(loc2);
        return Math.round(distance);
    }
}
