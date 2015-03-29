package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class PrikbordAdapter extends RecyclerView.Adapter<PrikbordViewHolder>  {
    private ArrayList<PrikbordItem> mData = new ArrayList<PrikbordItem>();
    private GeoLocationHelper locHelper = new GeoLocationHelper();
    private WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
    private Context context;

    public PrikbordAdapter(Context _context) {
        context = _context;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public PrikbordViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.snipet_prikbord_item, viewGroup, false);
        return new PrikbordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PrikbordViewHolder viewHolder, int position) {
        String distance = getDistanceString(position);

        viewHolder.adress.setText(mData.get(position).getAdres());
        if(distance == null)
            viewHolder.distance.setVisibility(View.GONE);
        else
            viewHolder.distance.setText(distance);
    }

    public void addItem(int position, PrikbordItem prikbordItem) {
        mData.add(position, prikbordItem);
        notifyItemInserted(position);
    }

    public boolean hasItem(PrikbordItem prikbordItem) {
        for(PrikbordItem pi : mData)
            if(pi.getId() == prikbordItem.getId())
                return true;
        return false;
    }

    private String getDistanceString(int position) {
        Location werkgebiedLocation = werkgebiedHelper.getFirstWerkgebiedLocation(context);

        if(werkgebiedLocation != null && mData.get(position).getLocation() != null) {
            if((werkgebiedLocation.getLatitude() == 0 && werkgebiedLocation.getLongitude() == 0) || (mData.get(position).getLocation().getLatitude() == 0 && mData.get(position).getLocation().getLongitude() == 0)) {
                return null;
            }else {

                int distance = locHelper.getDistanceBetweenLocations(werkgebiedLocation, mData.get(position).getLocation());
                if (distance < 1000) {
                    return Integer.toString(distance) + " Meter";
                } else {
                    return Float.toString((float) ((int) (distance / 100)) / 10f) + " Kilometer";
                }
            }
        }else{
            return null;
        }
    }

    public static <E> boolean containsInstance(List<E> list, Class<? extends E> clazz) {
        for (E e : list) {
            if (clazz.isInstance(e)) {
                return true;
            }
        }
        return false;
    }
}