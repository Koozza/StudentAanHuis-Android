package com.thijsdev.studentaanhuis.Kalender;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.R;

public class KalenderListItem extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView time;
    public LinearLayout kalender_min0,kalender_min15,kalender_min30,kalender_min45;
    public TextView kalender_min0_detail,kalender_min15_detail,kalender_min30_detail,kalender_min45_detail;


    private ClickListener clickListener;

    public KalenderListItem(View itemView) {
        super(itemView);
        time = (TextView) itemView.findViewById(R.id.calendar_time);
        kalender_min0 = (LinearLayout) itemView.findViewById(R.id.kalender_min0);
        kalender_min15 = (LinearLayout) itemView.findViewById(R.id.kalender_min15);
        kalender_min30 = (LinearLayout) itemView.findViewById(R.id.kalender_min30);
        kalender_min45 = (LinearLayout) itemView.findViewById(R.id.kalender_min45);

        kalender_min0_detail = (TextView) itemView.findViewById(R.id.kalender_detailfield0);
        kalender_min15_detail = (TextView) itemView.findViewById(R.id.kalender_detailfield15);
        kalender_min30_detail = (TextView) itemView.findViewById(R.id.kalender_detailfield30);
        kalender_min45_detail = (TextView) itemView.findViewById(R.id.kalender_detailfield45);
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
