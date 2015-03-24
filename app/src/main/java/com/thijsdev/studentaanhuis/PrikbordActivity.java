package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrikbordActivity extends Activity {
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prikbord);

        final PrikbordAdapter mAdapter = new PrikbordAdapter(this);

        PrikbordHTTPHandler PrikbordHttpHandler = new PrikbordHTTPHandler();
        LoginHTTPHandler lh = new LoginHTTPHandler();
        HttpClientClass client = HttpClientClass.getInstance();

        PrikbordHttpHandler.getPrikbordItems(client, this, new Callback() {
            @Override
            public void onTaskCompleted(String result) {

                Document doc = Jsoup.parse(result);
                Elements trs = doc.select("tr:has(td)");
                for (Element tr : trs) {
                    PrikbordItem pi = new PrikbordItem();
                    Elements tds = tr.select("td");
                    pi.setBeschrijving(tds.get(0).text());
                    pi.setAdres(tds.get(1).text());
                    pi.setDeadline(tds.get(2).text());

                    mAdapter.addItem(pi);
                }
            }
        });

        ListView lv = (ListView) findViewById(R.id.prikbordList);
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