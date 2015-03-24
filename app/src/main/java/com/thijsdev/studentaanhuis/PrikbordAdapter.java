package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class PrikbordAdapter extends BaseAdapter {
    private ArrayList<PrikbordItem> mData = new ArrayList<PrikbordItem>();
    private LayoutInflater mInflater;

    public PrikbordAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final PrikbordItem p) {
        mData.add(p);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.snipet_prikbord_item, null);
            holder.omschrijving = (TextView) convertView.findViewById(R.id.omschrijving);
            holder.locatie = (TextView) convertView.findViewById(R.id.locatie);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.omschrijving.setText( mData.get(position).getBeschrijving());
        holder.locatie.setText(mData.get(position).getAdres());

        return convertView;
    }

    public static class ViewHolder {
        public TextView omschrijving, locatie;
    }
}