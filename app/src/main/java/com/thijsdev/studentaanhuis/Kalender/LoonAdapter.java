package com.thijsdev.studentaanhuis.Kalender;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

class LoonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<Object> mData = new ArrayList<>();
    private Context context;

    public LoonAdapter(Context _context) {
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

        if(viewType == 0)
            itemView = inflater.inflate(R.layout.snipet_loon_item_small, viewGroup, false);
        else
            itemView = inflater.inflate(R.layout.snipet_loon_item, viewGroup, false);

        return new LoonListItem(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if(((LoonMaand)mData.get(position)).getLoonMogelijk() == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        LoonListItem loonListItem = (LoonListItem)viewHolder;

        //Check if we should substract VAT
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Double vat = 1d;

        if(sharedPref.getBoolean("loon_include_vat", false))
            vat = 0.635d;

        //Other information
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", new Locale("nl", "NL"));
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("nl", "NL"));

        loonListItem.maand.setText(dateFormat.format(((LoonMaand) mData.get(position)).getDatum()));
        loonListItem.verdiensten.setText(defaultFormat.format((((LoonMaand) mData.get(position)).getLoon() + ((LoonMaand) mData.get(position)).getLoonAndereMaand()) * vat));
        if(getItemViewType(position) == 1) {
            loonListItem.mogelijke_verdiensten.setText(defaultFormat.format(((LoonMaand) mData.get(position)).getLoonMogelijk() * vat));
            loonListItem.totaal_mogelijk_verdiensten.setText(defaultFormat.format((((LoonMaand) mData.get(position)).getLoon() + ((LoonMaand) mData.get(position)).getLoonMogelijk() + ((LoonMaand) mData.get(position)).getLoonAndereMaand()) * vat));
        }
        loonListItem.aantal_afspraken.setText(Integer.toString(((LoonMaand) mData.get(position)).getAfspraken()));
        loonListItem.aantal_servicevragen.setText(Integer.toString(((LoonMaand) mData.get(position)).getServicevragen()));

        //Set fonts
        loonListItem.maand.setTypeface(((MainActivity) context).robotoMedium);
        loonListItem.verdiensten.setTypeface(((MainActivity) context).robotoRegular);
        loonListItem.verdiensten_label.setTypeface(((MainActivity) context).robotoRegular);
        loonListItem.aantal_afspraken_label.setTypeface(((MainActivity) context).robotoRegular);
        loonListItem.aantal_afspraken.setTypeface(((MainActivity) context).robotoRegular);
        loonListItem.aantal_servicevragen_label.setTypeface(((MainActivity) context).robotoRegular);
        loonListItem.aantal_servicevragen.setTypeface(((MainActivity) context).robotoRegular);
        if(getItemViewType(position) == 1) {
            loonListItem.mogelijke_verdiensten_label.setTypeface(((MainActivity) context).robotoRegular);
            loonListItem.mogelijke_verdiensten.setTypeface(((MainActivity) context).robotoRegular);
            loonListItem.totaal_mogelijk_verdiensten_label.setTypeface(((MainActivity) context).robotoRegular);
            loonListItem.totaal_mogelijk_verdiensten.setTypeface(((MainActivity) context).robotoRegular);
        }

        //set empty click handler, to prevent crash
        loonListItem.setClickListener(new LoonListItem.ClickListener() {
            @Override
            public void onClick(View v, int pos) {
                //Nothing
            }
        });
    }

    public void addItem(int position, Object loonItem) {
        mData.add(position, loonItem);
        notifyItemInserted(position);
    }

    public void updateItem(Object loonItem) {
        int position = getPostition(loonItem);

        if(position == -1) {
            addItem(0, loonItem);
            notifyItemInserted(0);
        }else {
            mData.set(position, loonItem);
            notifyDataSetChanged();
        }
    }

    public int getPostition(Object loonItem) {
        int i = 0;
        for(Object obj : mData) {
            if (((LoonMaand) obj).getDatum().toString().equals(((LoonMaand)loonItem).getDatum().toString())) {
                return i;
            }
            i++;
        }

        return -1;
    }

    public Boolean containsItem(Object loonItem) {
        return mData.contains(loonItem);
    }
}