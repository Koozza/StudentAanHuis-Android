package com.thijsdev.studentaanhuis.Kalender;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.R;

public class KalenderListItem extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView maand;
    public TextView aantal_afspraken_label, aantal_afspraken;
    public TextView aantal_servicevragen_label, aantal_servicevragen;
    public TextView verdiensten_label, verdiensten;
    public TextView mogelijke_verdiensten_label, mogelijke_verdiensten;
    public TextView totaal_mogelijk_verdiensten_label, totaal_mogelijk_verdiensten;


    private ClickListener clickListener;

    public KalenderListItem(View itemView) {
        super(itemView);
        /*maand = (TextView) itemView.findViewById(R.id.loon_maand);
        aantal_afspraken_label = (TextView) itemView.findViewById(R.id.loon_aantal_afspraken_label);
        aantal_afspraken = (TextView) itemView.findViewById(R.id.loon_aantal_afspraken);
        aantal_servicevragen_label = (TextView) itemView.findViewById(R.id.loon_aantal_servicevragen_label);
        aantal_servicevragen = (TextView) itemView.findViewById(R.id.loon_aantal_servicevragen);
        mogelijke_verdiensten_label = (TextView) itemView.findViewById(R.id.loon_mogelijk_verdiensten_label);
        mogelijke_verdiensten = (TextView) itemView.findViewById(R.id.loon_mogelijk_verdiensten);
        totaal_mogelijk_verdiensten_label = (TextView) itemView.findViewById(R.id.loon_totaal_mogelijk_verdiensten_label);
        totaal_mogelijk_verdiensten = (TextView) itemView.findViewById(R.id.loon_totaal_mogelijk_verdiensten);
        verdiensten = (TextView) itemView.findViewById(R.id.loon_verdiensten);
        verdiensten_label = (TextView) itemView.findViewById(R.id.loon_verdiensten_label);
        itemView.findViewById(R.id.prikbord_item).setOnClickListener(this);*/
    }

    public interface ClickListener
    {
        public void onClick(View v, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v)
    {
        clickListener.onClick(v, getPosition());
    }
}
