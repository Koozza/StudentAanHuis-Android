package com.thijsdev.studentaanhuis;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PrikbordListHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView title;

    public PrikbordListHeader(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.prikbord_header_title);
    }

    @Override
    public void onClick(View v)
    {
        //Niks
    }
}
