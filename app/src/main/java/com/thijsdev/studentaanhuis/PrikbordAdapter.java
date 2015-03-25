package com.thijsdev.studentaanhuis;

import android.content.Context;
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

    public PrikbordAdapter(Context context) {
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
            holder.omschrijving = (TextView) convertView.findViewById(R.id.omschrijving);
            holder.statusImage = (ImageView) convertView.findViewById(R.id.statusImage);
            //holder.locatie = (TextView) convertView.findViewById(R.id.locatie);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.omschrijving.setText( mData.get(position).getBeschrijving());
        if(mData.get(position).getBeschikbaar() == 0)
            holder.statusImage.setImageDrawable(_context.getResources().getDrawable( R.drawable.minus ));
        else if(mData.get(position).getBeschikbaar() == 1)
            holder.statusImage.setImageDrawable(_context.getResources().getDrawable( R.drawable.denied ));
        else
            holder.statusImage.setImageDrawable(_context.getResources().getDrawable( R.drawable.check ));
        //holder.locatie.setText(mData.get(position).getAdres());

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
            holder.locatie = (TextView) convertView.findViewById(R.id.prikbord_locatie);
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
        holder.locatie.setText( mData.get(position).getAdres());
        //holder.locatie.setText(mData.get(position).getAdres());

        return convertView;
    }

    public static class ViewHolder {
        public TextView omschrijving, locatie, tags, status;
        public ImageView statusImage;
    }
}