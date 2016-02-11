package com.thijsdev.studentaanhuis.Kalender;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.Database.Afspraak;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;
public class AfspraakDetailFragment extends Fragment {
    private MainActivity mainActivity;
    private Toolbar toolbar;

    DatabaseHandler databaseHandler;
    Afspraak afspraak;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_afspraak_detail, container, false);

        mainActivity = (MainActivity) view.getContext();
        toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.afspraak_details));
        toolbar.inflateMenu(R.menu.menu_prikbord_detail);
        mainActivity.mDrawerToggle.setDrawerIndicatorEnabled(false);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mainActivity.mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });

        databaseHandler = DatabaseHandler.getInstance(getActivity());

        Bundle bundle = this.getArguments();
        int AfspraakId = bundle.getInt("AfspraakId", -1);
        afspraak = databaseHandler.getAfspraak(AfspraakId);

        //Set labels as bold
        ((TextView) view.findViewById(R.id.kalender_label_adres)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_klant)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_email)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_klantnummer)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_omschrijving)).setTypeface(((MainActivity)getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_pin)).setTypeface(((MainActivity) getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_tags)).setTypeface(((MainActivity) getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_telefoonnummer1)).setTypeface(((MainActivity) getActivity()).robotoMedium);
        ((TextView) view.findViewById(R.id.kalender_label_telefoonnummer2)).setTypeface(((MainActivity) getActivity()).robotoMedium);

        TextView kalenderAdres = (TextView) view.findViewById(R.id.kalender_adres);
        TextView kalenderKlant = (TextView) view.findViewById(R.id.kalender_klant);
        TextView kalenderEmail = (TextView) view.findViewById(R.id.kalender_email);
        TextView kalenderKlantnummer = (TextView) view.findViewById(R.id.kalender_klantnummer);
        TextView kalenderOmschrijving = (TextView) view.findViewById(R.id.kalender_description);
        TextView kalenderPin = (TextView) view.findViewById(R.id.kalender_pin);
        TextView kalenderTags = (TextView) view.findViewById(R.id.kalender_tags);
        TextView kalenderTelefoonnummer1 = (TextView) view.findViewById(R.id.kalender_telefoonnummer1);
        TextView kalenderTelefoonnummer2 = (TextView) view.findViewById(R.id.kalender_telefoonnummer2);

        kalenderAdres.setText(afspraak.getKlant().getAdres());
        kalenderKlant.setText(afspraak.getKlant().getNaam());
        kalenderEmail.setText(afspraak.getKlant().getEmail());
        kalenderKlantnummer.setText(afspraak.getKlant().getKlantnummer() + (afspraak.isNieuwLid() ? " (Nieuw Lid!)" : ""));
        kalenderOmschrijving.setText(afspraak.getOmschrijving());
        kalenderPin.setText(afspraak.getPin());
        kalenderTags.setText(afspraak.getTags());
        kalenderTelefoonnummer1.setText(afspraak.getKlant().getTel1());
        kalenderTelefoonnummer2.setText(afspraak.getKlant().getTel2());


        if(afspraak.getPin() == null) {
            view.findViewById(R.id.kalender_pin_row).setVisibility(View.GONE);
        }

        if(afspraak.getKlant().getTel1() == null) {
            view.findViewById(R.id.kalender_telefoonnummer1_row).setVisibility(View.GONE);
        }

        if(afspraak.getKlant().getTel2() == null) {
            view.findViewById(R.id.kalender_telefoonnummer2_row).setVisibility(View.GONE);
        }

        return view;
    }
}
