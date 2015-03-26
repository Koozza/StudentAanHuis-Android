package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

class PrikbordAdapter extends BaseExpandableListAdapter {
    private ArrayList<PrikbordItem> mData = new ArrayList<PrikbordItem>();
    private LayoutInflater mInflater;
    private Context _context;
    private GeoLocationHelper locHelper = new GeoLocationHelper();
    private WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
    final private PrikbordHelper prikbordHelper = new PrikbordHelper();

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


    public View getGroupView(final int position, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.snipet_prikbord_item, null);
            holder.locatie = (TextView) convertView.findViewById(R.id.prikbord_locatie);
            holder.AcceptedImage = (ImageView) convertView.findViewById(R.id.statusImageAccepted);
            holder.DeclinedImage = (ImageView) convertView.findViewById(R.id.statusImageDeclined);
            holder.Distance = (TextView) convertView.findViewById(R.id.prikbord_afstand);
            convertView.setTag(holder);

            holder.DeclinedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    declineOnClick(position);
                }
            });

            holder.AcceptedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptOnClick(position);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(mData.get(position).getBeschikbaar() == 1) {
            holder.DeclinedImage.setImageDrawable(_context.getResources().getDrawable(R.drawable.denied));
            holder.AcceptedImage.setImageDrawable(_context.getResources().getDrawable(R.drawable.check_light));
        } else if(mData.get(position).getBeschikbaar() == 2) {
            holder.DeclinedImage.setImageDrawable(_context.getResources().getDrawable(R.drawable.denied_light));
            holder.AcceptedImage.setImageDrawable(_context.getResources().getDrawable(R.drawable.check));
        }else{
            holder.DeclinedImage.setImageDrawable(_context.getResources().getDrawable(R.drawable.denied_light));
            holder.AcceptedImage.setImageDrawable(_context.getResources().getDrawable(R.drawable.check_light));
        }

        holder.locatie.setText(mData.get(position).getAdres());

        int distance = locHelper.getDistanceBetweenLocations(werkgebiedHelper.getFirstWerkgebiedLocation(_context), mData.get(position).getLocation());
        if(distance < 1000) {
            holder.Distance.setText(Integer.toString(distance) + " Meter");
        }else {
            holder.Distance.setText(Float.toString((float) ((int) (distance / 100)) / 10f) + " Kilometer");
        }

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
            holder.description = (TextView) convertView.findViewById(R.id.prikbord_description);
            holder.deadline = (TextView) convertView.findViewById(R.id.prikbord_deadline);
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

        holder.description.setText( mData.get(position).getBeschrijving());
        holder.deadline.setText( mData.get(position).getFormatedDeadline("EEE dd MMMM yyyy", null));

        return convertView;
    }

    public static class ViewHolder {
        public TextView description, locatie, tags, status, Distance, deadline;
        public ImageView AcceptedImage, DeclinedImage;
    }

    private void declineOnClick(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final RelativeLayout loadingScreen = (RelativeLayout) ((Activity)_context).findViewById(R.id.prikbord_loading);
                        loadingScreen.setVisibility(View.VISIBLE);

                        prikbordHelper.declineItem(_context, mData.get(position), new Callback() {
                            @Override
                            public void onTaskCompleted(String result) {
                                loadingScreen.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage("Weet je zeker dat je deze afspraak niet kan doen?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nee", dialogClickListener).show();
    }

    private void acceptOnClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setTitle(_context.getString(R.string.when_availible));

        final EditText input = new EditText(_context);
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(3);
        input.setGravity(Gravity.TOP | Gravity.LEFT);
        builder.setView(input);

        builder.setPositiveButton("Verder", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userinput = input.getText().toString().trim();
                if(!userinput.equals("")) {
                    showWerkgebiedDialog(position, userinput);
                }else{
                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showWerkgebiedDialog(final int position, final String beschikbaarheid) {
        final WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setTitle(_context.getString(R.string.when_availible));

        builder.setItems(werkgebiedHelper.getWerkgebiedenArray(_context), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Werkgebied werkgebied = werkgebiedHelper.getActiveWerkgebieden(_context).get(item);
                final RelativeLayout loadingScreen = (RelativeLayout) ((Activity)_context).findViewById(R.id.prikbord_loading);
                loadingScreen.setVisibility(View.VISIBLE);

                prikbordHelper.acceptItem(_context, mData.get(position), beschikbaarheid, werkgebied, new Callback() {
                    @Override
                    public void onTaskCompleted(String result) {
                        loadingScreen.setVisibility(View.GONE);
                        notifyDataSetChanged();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}