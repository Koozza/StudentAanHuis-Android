package com.thijsdev.studentaanhuis;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PrikbordViewHolder extends RecyclerView.ViewHolder {
    public TextView adress, distance, omschrijving;

    public PrikbordViewHolder(View itemView) {
        super(itemView);
        adress = (TextView) itemView.findViewById(R.id.prikbord_locatie);
        distance = (TextView) itemView.findViewById(R.id.prikbord_afstand);
        omschrijving = (TextView) itemView.findViewById(R.id.prikbord_omschrijving);
    }
}
