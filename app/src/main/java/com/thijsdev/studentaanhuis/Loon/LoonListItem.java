package com.thijsdev.studentaanhuis.Loon;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.R;

public class LoonListItem extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView maand, verdiensten;
    private ClickListener clickListener;

    public LoonListItem(View itemView) {
        super(itemView);
        maand = (TextView) itemView.findViewById(R.id.loon_maand);
        verdiensten = (TextView) itemView.findViewById(R.id.loon_verdiensten);
        itemView.findViewById(R.id.prikbord_item).setOnClickListener(this);
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
