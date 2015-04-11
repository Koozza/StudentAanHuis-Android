package com.thijsdev.studentaanhuis;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrikbordDetailFragment extends Fragment {
    final private PrikbordHelper prikbordHelper = new PrikbordHelper();
    private WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
    private GeoLocationHelper locHelper = new GeoLocationHelper();

    private MainActivity mainActivity;
    private Toolbar toolbar;

    DatabaseHandler databaseHandler;
    PrikbordItem prikbordItem;

    private String locationId, omschrijvingId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_prikbord_detail, container, false);

        mainActivity = (MainActivity) view.getContext();
        toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.prikbord_item));
        toolbar.inflateMenu(R.menu.menu_prikbord_detail);
        mainActivity.mDrawerToggle.setDrawerIndicatorEnabled(false);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mainActivity.mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });

        databaseHandler = new DatabaseHandler(getActivity());

        Bundle bundle = this.getArguments();
        int PrikbordId = bundle.getInt("PrikbordId", -1);
        prikbordItem = databaseHandler.getPrikbordItem(PrikbordId);
        String distance = getDistanceString(prikbordItem);

        //Set labels as bold
        ((TextView) view.findViewById(R.id.prikbord_label_afstand)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_deadline)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_locatie)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_status)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_tags)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.prikbord_label_omschrijving)).setTypeface(((MainActivity) getActivity()).robotoMedium);

        TextView prikbordLocatie = (TextView) view.findViewById(R.id.prikbord_locatie);
        TextView prikbordDistance = (TextView) view.findViewById(R.id.prikbord_afstand);
        TextView prikbordStatus = (TextView) view.findViewById(R.id.prikbord_status);
        TextView prikbordDeadline = (TextView) view.findViewById(R.id.prikbord_deadline);
        TextView prikbordTags = (TextView) view.findViewById(R.id.prikbord_tags);
        TextView prikbordDescription = (TextView) view.findViewById(R.id.prikbord_description);

        prikbordDescription.setText(prikbordItem.getBeschrijving());
        prikbordTags.setText(prikbordItem.getType());

        //Fix adress to strip postcode
        Pattern p = Pattern.compile("(\\w+), \\d+ \\w+\\s+(\\w+)");
        Matcher m = p.matcher(prikbordItem.getAdres());
        m.find();
        prikbordLocatie.setText(m.group(1) + ", " + m.group(2));


        //set deadline
        prikbordDeadline.setText(prikbordItem.getFormatedDeadline("EEE dd MMMM yyyy", null));

        //set distance
        if(distance == null)
            prikbordDistance.setVisibility(View.GONE);
        else
            prikbordDistance.setText(distance);

        prikbordLocatie.setTypeface(((MainActivity)getActivity()).robotoRegular);
        prikbordDistance.setTypeface(((MainActivity)getActivity()).robotoRegular);
        prikbordDescription.setTypeface(((MainActivity) getActivity()).robotoRegular);

        Button aanmelden = ((Button) view.findViewById(R.id.btn_prikbord_detail_aanmelden));
        Button afmelden = ((Button) view.findViewById(R.id.btn_prikbord_detail_afmelden));
        aanmelden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prikbordItem.getBeschikbaar() == 0 || prikbordItem.getBeschikbaar() == 1)
                    acceptOnClick(prikbordItem, view);
            }
        });
        afmelden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prikbordItem.getBeschikbaar() == 0 || prikbordItem.getBeschikbaar() == 2)
                    declineOnClick(prikbordItem, view);
            }
        });

        //Set status & Buttons
        updateStatus(view);

        return view;
    }

    private void updateStatus(View view) {
        Button aanmelden = ((Button) view.findViewById(R.id.btn_prikbord_detail_aanmelden));
        Button afmelden = ((Button) view.findViewById(R.id.btn_prikbord_detail_afmelden));
        TextView prikbordStatus = (TextView) view.findViewById(R.id.prikbord_status);

        //Button colors
        if(prikbordItem.getBeschikbaar() == 2) {
            aanmelden.setTextColor(getResources().getColor(R.color.SAHlightblue));
            afmelden.setTextColor(getResources().getColor(R.color.SAHdarkblue));
            prikbordStatus.setText(mainActivity.getString(R.string.available));

            aanmelden.setClickable(false);
            afmelden.setClickable(true);
        }if(prikbordItem.getBeschikbaar() == 1) {
            afmelden.setTextColor(getResources().getColor(R.color.SAHlightblue));
            aanmelden.setTextColor(getResources().getColor(R.color.SAHdarkblue));
            prikbordStatus.setText(mainActivity.getString(R.string.unavailable));

            aanmelden.setClickable(true);
            afmelden.setClickable(false);
        }if(prikbordItem.getBeschikbaar() == 0) {
            afmelden.setTextColor(getResources().getColor(R.color.SAHdarkblue));
            aanmelden.setTextColor(getResources().getColor(R.color.SAHdarkblue));
            prikbordStatus.setText(mainActivity.getString(R.string.none));

            aanmelden.setClickable(true);
            afmelden.setClickable(true);
        }
    }

    private String getDistanceString(PrikbordItem prikbordItem) {
        Location werkgebiedLocation = werkgebiedHelper.getFirstWerkgebiedLocation(getActivity());

        if(werkgebiedLocation != null && prikbordItem.getLocation() != null) {
            if((werkgebiedLocation.getLatitude() == 0 && werkgebiedLocation.getLongitude() == 0) || (prikbordItem.getLocation().getLatitude() == 0 && prikbordItem.getLocation().getLongitude() == 0)) {
                return null;
            }else {

                int distance = locHelper.getDistanceBetweenLocations(werkgebiedLocation, prikbordItem.getLocation());
                if (distance < 1000) {
                    return Integer.toString(distance) + "M";
                } else {
                    return Float.toString((float) ((int) (distance / 100)) / 10f) + "Km";
                }
            }
        }else{
            return null;
        }
    }



    private void declineOnClick(final PrikbordItem prikbordItem, final View view) {
        //Oude code met confirm; weggehaald op verzoek
        /*
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final RelativeLayout loadingScreen = (RelativeLayout) mainActivity.findViewById(R.id.main_loading);
                        loadingScreen.setVisibility(View.VISIBLE);

                        prikbordHelper.declineItem(mainActivity, prikbordItem, new Callback() {
                            @Override
                            public void onTaskCompleted(Object result) {
                                loadingScreen.setVisibility(View.GONE);
                                ((PrikbordAdapter) mainActivity.getSharedObject("prikbordAdapter")).notifyDataSetChanged();
                                updateStatus(view);
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setMessage(mainActivity.getString(R.string.sure_you_cant_do_appointment)).setPositiveButton(mainActivity.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(mainActivity.getString(R.string.no), dialogClickListener).show();
                */

        final RelativeLayout loadingScreen = (RelativeLayout) mainActivity.findViewById(R.id.main_loading);
        loadingScreen.setVisibility(View.VISIBLE);

        prikbordHelper.declineItem(mainActivity, prikbordItem, new Callback() {
            @Override
            public void onTaskCompleted(Object result) {
                loadingScreen.setVisibility(View.GONE);
                PrikbordAdapter prikbordAdapter = ((PrikbordAdapter) mainActivity.getSharedObject("prikbordAdapter"));
                prikbordAdapter.moveItem(prikbordAdapter.findItem(prikbordItem.getId()), prikbordAdapter.findItem(prikbordItem.getBeschikbaar()) + 1);
                prikbordAdapter.notifyDataSetChanged();
                updateStatus(view);
                mainActivity.onBackPressed();
            }
        });
    }

    private void acceptOnClick(final PrikbordItem prikbordItem, final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(mainActivity.getString(R.string.when_availible));

        final EditText input = new EditText(mainActivity);
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(3);
        input.setGravity(Gravity.TOP | Gravity.LEFT);
        input.setTextColor(getResources().getColor(R.color.black87));
        input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        builder.setView(input);

        builder.setPositiveButton(mainActivity.getString(R.string.continu), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userinput = input.getText().toString().trim();
                if(!userinput.equals("")) {
                    showWerkgebiedDialog(prikbordItem, view, userinput);
                }else{
                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton(mainActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showWerkgebiedDialog(final PrikbordItem prikbordItem, final View view, final String beschikbaarheid) {
        final WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(mainActivity.getString(R.string.select_workarea));

        builder.setItems(werkgebiedHelper.getWerkgebiedenArray(mainActivity), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Werkgebied werkgebied = werkgebiedHelper.getActiveWerkgebieden(mainActivity).get(item);
                final RelativeLayout loadingScreen = (RelativeLayout) mainActivity.findViewById(R.id.main_loading);
                loadingScreen.setVisibility(View.VISIBLE);

                prikbordHelper.acceptItem(mainActivity, prikbordItem, beschikbaarheid, werkgebied, new Callback() {
                    @Override
                    public void onTaskCompleted(Object result) {
                        loadingScreen.setVisibility(View.GONE);
                        PrikbordAdapter prikbordAdapter = ((PrikbordAdapter) mainActivity.getSharedObject("prikbordAdapter"));
                        prikbordAdapter.moveItem(prikbordAdapter.findItem(prikbordItem.getId()), prikbordAdapter.findItem(prikbordItem.getBeschikbaar()) + 1);
                        prikbordAdapter.notifyDataSetChanged();
                        updateStatus(view);
                        mainActivity.onBackPressed();
                    }
                });
            }
        });

        builder.setNegativeButton(mainActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
