package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        //Fix adress to strip postcode
        Pattern p = Pattern.compile("(\\w+), \\d+ \\w+\\s+(\\w+)");
        Matcher m = p.matcher(mData.get(position).getAdres());
        m.find();
        viewHolder.adress.setText(m.group(1)+", "+m.group(2));

        //Other information
        viewHolder.omschrijving.setText(mData.get(position).getBeschrijving());
        if(distance == null)
            viewHolder.distance.setVisibility(View.GONE);
        else
            viewHolder.distance.setText(distance);

        viewHolder.adress.setTypeface(((MainActivity)context).robotoMedium);
        viewHolder.distance.setTypeface(((MainActivity)context).robotoRegular);
        viewHolder.omschrijving.setTypeface(((MainActivity) context).robotoRegular);

        /*
        if(android.os.Build.VERSION.SDK_INT >= 21) {
            viewHolder.adress.setTransitionName("adress" + position);
            viewHolder.omschrijving.setTransitionName("omschrijving" + position);
        }
        */

        viewHolder.setClickListener(new PrikbordViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int pos) {
                PrikbordDetailFragment fragment = new PrikbordDetailFragment();

                /*
                View title = v.findViewById(R.id.prikbord_locatie);
                View desc = v.findViewById(R.id.prikbord_omschrijving);

                if(android.os.Build.VERSION.SDK_INT >= 21) {
                    fragment.setSharedElementEnterTransition(TransitionInflater.from(context).inflateTransition(R.transition.trans_move));
                    fragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.explode));
                    fragment.setLocationId(title.getTransitionName());
                    fragment.setOmschrijvingId(desc.getTransitionName());
                }
                */

                Bundle bundle = new Bundle();
                bundle.putInt("PrikbordId", mData.get(pos).getId());
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((Activity)context).getFragmentManager().beginTransaction();

                transaction.replace(R.id.prikbord_fragments, fragment);
                transaction.addToBackStack(null);
                /*
                if(android.os.Build.VERSION.SDK_INT >= 21) {
                    transaction.addSharedElement(title, title.getTransitionName());
                    transaction.addSharedElement(desc, desc.getTransitionName());
                }
                */
                transaction.commit();
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