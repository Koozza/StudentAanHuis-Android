package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

public class PrikbordActivity extends Activity {
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prikbord);

        final PrikbordAdapter mAdapter = new PrikbordAdapter(this);

        PrikbordHelper prikbordHelper = new PrikbordHelper();
        prikbordHelper.updatePrikbordItems(this, mAdapter);

        ExpandableListView lv = (ExpandableListView) findViewById(R.id.prikbordList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO: Deze click moet natuurlijk de info door gaan geven naar de volgende activity.
                //TODO: Click reageert ook op separators! Let hiermee op, dat mag natuurlijk niks doen
            }
        });
        lv.setAdapter(mAdapter);
    }
}