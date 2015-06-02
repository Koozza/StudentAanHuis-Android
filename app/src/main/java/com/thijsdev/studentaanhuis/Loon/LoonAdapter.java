package com.thijsdev.studentaanhuis.Loon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;

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

        itemView = inflater.inflate(R.layout.snipet_loon_item, viewGroup, false);
        return new LoonListItem(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        LoonListItem loonListItem = (LoonListItem)viewHolder;

        //Other information
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", new Locale("nl", "NL"));

        loonListItem.maand.setText(dateFormat.format(((LoonMaand) mData.get(position)).getDatum()));
        loonListItem.verdiensten.setText("\u20ac "+Double.toString(((LoonMaand)mData.get(position)).getLoonZeker()));
        loonListItem.maand.setTypeface(((MainActivity) context).robotoMedium);
        loonListItem.verdiensten.setTypeface(((MainActivity) context).robotoRegular);
    }

    public void addItem(int position, Object loonItem) {
        mData.add(position, loonItem);
        notifyItemInserted(position);
    }
}