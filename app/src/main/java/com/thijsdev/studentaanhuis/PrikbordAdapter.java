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

class PrikbordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<Object> mData = new ArrayList<>();
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView;
        switch (viewType) {
            case 0:
                itemView = inflater.inflate(R.layout.snipet_prikbord_item, viewGroup, false);
                return new PrikbordListItem(itemView);
            case 1:
                itemView = inflater.inflate(R.layout.snipet_prikbord_header, viewGroup, false);
                return new PrikbordListHeader(itemView);
            case 2:
                itemView = inflater.inflate(R.layout.snipet_prikbord_message, viewGroup, false);
                return new PrikbordListHeader(itemView);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if(mData.get(position) instanceof PrikbordItem)
            return 0;
        else if(!((PrikbordHeader)mData.get(position)).isMessage())
            return 1;
        else
            return 2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof PrikbordListItem) {
            PrikbordListItem prikbordListItem = (PrikbordListItem)viewHolder;
            String distance = getDistanceString(position);

            //Fix adress to strip postcode
            Pattern p = Pattern.compile("(\\w+), \\d+ \\w+\\s+(\\w+)");
            Matcher m = p.matcher(((PrikbordItem)mData.get(position)).getAdres());
            m.find();
            prikbordListItem.adress.setText(m.group(1) + ", " + m.group(2));

            //Other information
            prikbordListItem.omschrijving.setText(((PrikbordItem)mData.get(position)).getBeschrijving());
            if (distance == null)
                prikbordListItem.distance.setVisibility(View.GONE);
            else
                prikbordListItem.distance.setText(distance);

            prikbordListItem.adress.setTypeface(((MainActivity) context).robotoMedium);
            prikbordListItem.distance.setTypeface(((MainActivity) context).robotoRegular);
            prikbordListItem.omschrijving.setTypeface(((MainActivity) context).robotoRegular);

            prikbordListItem.setClickListener(new PrikbordListItem.ClickListener() {
                @Override
                public void onClick(View v, int pos) {
                    PrikbordDetailFragment fragment = new PrikbordDetailFragment();

                    Bundle bundle = new Bundle();
                    bundle.putInt("PrikbordId", ((PrikbordItem)mData.get(pos)).getId());
                    fragment.setArguments(bundle);

                    FragmentTransaction transaction = ((Activity) context).getFragmentManager().beginTransaction();

                    transaction.replace(R.id.prikbord_fragments, fragment);
                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            });
        }else{
            PrikbordListHeader prikbordListHeader = (PrikbordListHeader)viewHolder;

            if(!((PrikbordHeader)mData.get(position)).isMessage()) {
                prikbordListHeader.title.setTypeface(((MainActivity) context).robotoBold);
                prikbordListHeader.title.setText(((PrikbordHeader) mData.get(position)).getTitle());
            }else{
                prikbordListHeader.title.setTypeface(((MainActivity) context).robotoLight);
                prikbordListHeader.title.setText(((PrikbordHeader) mData.get(position)).getTitle());
            }
        }
    }

    public void addItem(int position, Object prikbordItem) {
        mData.add(position, prikbordItem);
        notifyItemInserted(position);
    }

    public void moveItem(int oldPosition, int newPosition) {
        if(oldPosition < newPosition)
            newPosition -= 1;

        Object item = mData.get(oldPosition);
        mData.remove(oldPosition);
        mData.add(newPosition, item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    //TODO: Lompe functie, kan netter...
    public int findItem(int id) {
        int index = 0;
        for(Object pi : mData) {
            if (pi instanceof PrikbordItem)
                if (((PrikbordItem) pi).getId() == id)
                    return index;

            if (pi instanceof PrikbordHeader)
                if (((PrikbordHeader) pi).getId() == id)
                    return index;

            index++;
        }
        return -1;
    }

    public boolean hasItem(PrikbordItem prikbordItem) {
        for(Object pi : mData)
            if(pi instanceof PrikbordItem)
                if(((PrikbordItem)pi).getId() == prikbordItem.getId())
                    return true;

        return false;
    }

    private String getDistanceString(int position) {
        Location werkgebiedLocation = werkgebiedHelper.getFirstWerkgebiedLocation(context);

        if(werkgebiedLocation != null && ((PrikbordItem)mData.get(position)).getLocation() != null) {
            if((werkgebiedLocation.getLatitude() == 0 && werkgebiedLocation.getLongitude() == 0) || (((PrikbordItem)mData.get(position)).getLocation().getLatitude() == 0 && ((PrikbordItem)mData.get(position)).getLocation().getLongitude() == 0)) {
                return null;
            }else {

                int distance = locHelper.getDistanceBetweenLocations(werkgebiedLocation, ((PrikbordItem)mData.get(position)).getLocation());
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