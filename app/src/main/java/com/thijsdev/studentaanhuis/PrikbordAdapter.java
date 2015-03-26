package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class PrikbordAdapter extends BaseExpandableListAdapter {
    private ArrayList<PrikbordItem> mData = new ArrayList<PrikbordItem>();
    private LayoutInflater mInflater;
    private Context _context;
    private GeoLocationHelper locHelper = new GeoLocationHelper();
    private Location homeAdress;

    public PrikbordAdapter(Context context) {
        homeAdress = locHelper.getLocationFromAddress(context, "Van Delfthof 219, 5038 BX  Tilburg, The Netherlands");

        _context = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final PrikbordItem p) {
        mData.add(p);
        notifyDataSetChanged();
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    public int getGroupCount() {
        return mData.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public PrikbordItem getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    public PrikbordItem getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition);
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }


    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.snipet_prikbord_item, null);
            holder.locatie = (TextView) convertView.findViewById(R.id.prikbord_locatie);
            holder.AcceptedImage = (ImageView) convertView.findViewById(R.id.statusImageAccepted);
            holder.DeclinedImage = (ImageView) convertView.findViewById(R.id.statusImageDeclined);
            holder.Distance = (TextView) convertView.findViewById(R.id.prikbord_afstand);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(mData.get(position).getBeschikbaar() == 1)
            holder.DeclinedImage.setImageDrawable(_context.getResources().getDrawable( R.drawable.denied ));
        else if(mData.get(position).getBeschikbaar() == 2)
            holder.AcceptedImage.setImageDrawable(_context.getResources().getDrawable( R.drawable.check ));

        holder.locatie.setText(mData.get(position).getAdres());

        Location temp = new Location("");
        temp.setLatitude(51.5289677);
        temp.setLongitude(5.02125209999997);


        int distance = locHelper.getDistanceBetweenLocations(homeAdress, temp);
        if(distance < 1000) {
            holder.Distance.setText(Integer.toString(distance) + " Meter");
        }else {
            holder.Distance.setText(Float.toString((float) ((int) (distance / 100)) / 10f) + " Kilometer");
        }

        return convertView;
    }

    @Override
    public View getChildView(int position, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.snipet_prikbord_child, null);
            holder.tags = (TextView) convertView.findViewById(R.id.prikbord_tags);
            holder.status = (TextView) convertView.findViewById(R.id.prikbord_status);
            holder.description = (TextView) convertView.findViewById(R.id.prikbord_description);
            holder.deadline = (TextView) convertView.findViewById(R.id.prikbord_deadline);
            //holder.locatie = (TextView) convertView.findViewById(R.id.locatie);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tags.setText( mData.get(position).getType());
        if(mData.get(position).getBeschikbaar() == 0)
            holder.status.setText(_context.getString(R.string.none));
        else if(mData.get(position).getBeschikbaar() == 1)
            holder.status.setText(_context.getString(R.string.unavailable));
        else
            holder.status.setText( _context.getString(R.string.available));

        holder.description.setText( mData.get(position).getBeschrijving());
        holder.deadline.setText( mData.get(position).getFormatedDeadline("EEE dd MMMM yyyy", null));

        return convertView;
    }

    public static class ViewHolder {
        public TextView description, locatie, tags, status, Distance, deadline;
        public ImageView AcceptedImage, DeclinedImage;
    }
}