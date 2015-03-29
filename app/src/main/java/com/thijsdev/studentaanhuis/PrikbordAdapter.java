package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
        viewHolder.omschrijving.setText(mData.get(position).getBeschrijving());
        if(distance == null)
            viewHolder.distance.setVisibility(View.GONE);
        else
            viewHolder.distance.setText(distance);

        viewHolder.adress.setTypeface(((PrikbordActivity)context).robotoMedium);
        viewHolder.distance.setTypeface(((PrikbordActivity)context).robotoRegular);
        viewHolder.omschrijving.setTypeface(((PrikbordActivity) context).robotoRegular);

        viewHolder.setClickListener(new PrikbordViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int pos) {
                Log.v("SAH", "Click");
                Intent goToNextActivity = new Intent(context, PrikbordDetailActivity.class);
                context.startActivity(goToNextActivity);
            }
        });
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