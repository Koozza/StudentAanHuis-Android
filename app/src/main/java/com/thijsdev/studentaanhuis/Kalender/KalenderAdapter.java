package com.thijsdev.studentaanhuis.Kalender;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thijsdev.studentaanhuis.Database.Afspraak;
import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

class KalenderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<Object> mData = new ArrayList<>();
    private Context context;

    public KalenderAdapter(Context _context) {
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

        itemView = inflater.inflate(R.layout.snipet_kalender_item, viewGroup, false);

        return new KalenderListItem(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
        /*
        if(((LoonMaand)mData.get(position)).getLoonMogelijk() == 0)
            return 0;
        else
            return 1;*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        KalenderListItem kalenderListItem = (KalenderListItem)viewHolder;
        kalenderListItem.time.setText(Integer.toString(((AgendaItem) mData.get(position)).getHour()) + ":00");

        kalenderListItem.kalender_min0.setBackgroundColor(0);
        kalenderListItem.kalender_min15.setBackgroundColor(0);
        kalenderListItem.kalender_min30.setBackgroundColor(0);
        kalenderListItem.kalender_min45.setBackgroundColor(0);

        kalenderListItem.kalender_min0_detail.setText("");
        kalenderListItem.kalender_min15_detail.setText("");
        kalenderListItem.kalender_min30_detail.setText("");
        kalenderListItem.kalender_min45_detail.setText("");


        for(Afspraak afspraak : ((AgendaItem)mData.get(position)).getAfspraken()) {
            Calendar begin = Calendar.getInstance();
            begin.setTime(afspraak.getStart());

            Calendar end = Calendar.getInstance();
            end.setTime(afspraak.getEnd());
            if(position >= begin.get(Calendar.HOUR_OF_DAY) - 6 && position <= end.get(Calendar.HOUR_OF_DAY) - 6) {
                long diff = afspraak.getEnd().getTime() - afspraak.getStart().getTime();
                int kwartieren = ((int) (diff / (60 * 1000) / 15)) % 4;
                if(kwartieren == 0)
                    kwartieren = 4;


                if(kwartieren >= 1) {
                    kalenderListItem.kalender_min0.setBackgroundColor(context.getResources().getColor(R.color.SAHlightblue));
                }
                if(kwartieren >= 2) {
                    kalenderListItem.kalender_min15.setBackgroundColor(context.getResources().getColor(R.color.SAHlightblue));
                }
                if(kwartieren >= 3) {
                    kalenderListItem.kalender_min30.setBackgroundColor(context.getResources().getColor(R.color.SAHlightblue));
                }
                if(kwartieren >= 4) {
                    kalenderListItem.kalender_min45.setBackgroundColor(context.getResources().getColor(R.color.SAHlightblue));
                }

                //Begint op een heel uur
                if(begin.get(Calendar.HOUR_OF_DAY) - 6 == position && (begin.get(Calendar.MINUTE) / 15) == 0) {
                    kalenderListItem.kalender_min0_detail.setText(afspraak.getKlant().getNaam());
                    kalenderListItem.kalender_min15_detail.setText(afspraak.getKlant().getKlantnummer());
                    kalenderListItem.kalender_min30_detail.setText(afspraak.getPin());
                }

            }
        }

        /*
        //Dit gaat mis omdat je vanaf hiet niet de VOLGENDE viewholder kan aanpassen.
        for(int i = 0; i <= ((AgendaItem) mData.get(position)).getDuration() / 4; i++) {

            if(kwartieren >= 1) {
                if (((AgendaItem) mData.get(position)).getStart() == 0 && ((AgendaItem) mData.get(position)).getHour() - 7 == position) {
                    kalenderListItem.kalender_min0.setBackgroundColor(context.getResources().getColor(R.color.black87));
                }
            }
            if(kwartieren >= 2) {
                if (((AgendaItem) mData.get(position)).getStart() == 0 && ((AgendaItem) mData.get(position)).getHour() - 7 == position) {
                    kalenderListItem.kalender_min15.setBackgroundColor(context.getResources().getColor(R.color.black87));
                }
            }
            if(kwartieren >= 3) {
                if (((AgendaItem) mData.get(position)).getStart() == 0 && ((AgendaItem) mData.get(position)).getHour() - 7 == position) {
                    kalenderListItem.kalender_min30.setBackgroundColor(context.getResources().getColor(R.color.black87));
                }
            }
            if(kwartieren >= 4) {
                if (((AgendaItem) mData.get(position)).getStart() == 0 && ((AgendaItem) mData.get(position)).getHour() - 7 == position) {
                    kalenderListItem.kalender_min45.setBackgroundColor(context.getResources().getColor(R.color.black87));
                }
            }
        }
*/
        //set empty click handler, to prevent crash
        kalenderListItem.setClickListener(new KalenderListItem.ClickListener() {
            @Override
            public void onClick(View v, int pos) {
                //Nothing
            }
        });
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void addItem(int position, Object loonItem) {
        mData.add(position, loonItem);
        notifyItemInserted(position);
    }

    public Object getItemByHour(int hour) {
        for(Object obj : mData) {
            if (((AgendaItem) obj).getHour() == hour) {
                return obj;
            }
        }
        return null;
    }

    public void setItem(int position, Object loonItem) {
        mData.set(position, loonItem);
        notifyItemChanged(position);
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
            if (((AgendaItem) obj).getHour() == ((AgendaItem)loonItem).getHour()) {
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