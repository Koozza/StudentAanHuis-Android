package com.thijsdev.studentaanhuis.Kalender;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.Database.Afspraak;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.FragmentInterface;
import com.thijsdev.studentaanhuis.MainActivity;
import com.thijsdev.studentaanhuis.R;

import java.util.Calendar;
import java.util.List;

public class KalenderListFragment extends Fragment implements FragmentInterface {
    DatabaseHandler db;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mainActivity;
    private Toolbar toolbar;

    public KalenderAdapter mAdapter;

    private boolean isRefreshing = false;

    // we name the left, middle and right page
    private static final int PAGE_LEFT = 0;
    private static final int PAGE_MIDDLE = 1;
    private static final int PAGE_RIGHT = 2;


    private LayoutInflater mInflater;
    private int mSelectedPageIndex = 1;
    // we save each page in a model
    private PageModel[] mPageModel = new PageModel[3];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_vp, container, false);


        mainActivity = (MainActivity) view.getContext();
        db = DatabaseHandler.getInstance(mainActivity);
        toolbar = mainActivity.getToolbar();
        toolbar.getMenu().clear();
        toolbar.setTitle(getString(R.string.calendar));
        toolbar.inflateMenu(R.menu.menu_loon);
        toolbar.setNavigationIcon(null);

        registerToolbarClick();


        // initializing the model
        initPageModel();

        mInflater = getActivity().getLayoutInflater();
        MyagerAdaper adapter = new MyagerAdaper();

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        // we dont want any smoothscroll. This enables us to switch the page
        // without the user notifiying this
        viewPager.setCurrentItem(PAGE_MIDDLE, false);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {

                    final PageModel leftPage = mPageModel[PAGE_LEFT];
                    final PageModel middlePage = mPageModel[PAGE_MIDDLE];
                    final PageModel rightPage = mPageModel[PAGE_RIGHT];

                    final int oldLeftIndex = leftPage.getIndex();
                    final int oldMiddleIndex = middlePage.getIndex();
                    final int oldRightIndex = rightPage.getIndex();

                    // user swiped to right direction --> left page
                    if (mSelectedPageIndex == PAGE_LEFT) {

                        // moving each page activity_kalender one page to the right
                        leftPage.setIndex(oldLeftIndex - 1);
                        middlePage.setIndex(oldLeftIndex);
                        rightPage.setIndex(oldMiddleIndex);

                        // user swiped to left direction --> right page
                    } else if (mSelectedPageIndex == PAGE_RIGHT) {

                        leftPage.setIndex(oldMiddleIndex);
                        middlePage.setIndex(oldRightIndex);
                        rightPage.setIndex(oldRightIndex + 1);
                    }

                    setContent(PAGE_MIDDLE);
                    viewPager.setCurrentItem(PAGE_MIDDLE, false);
                    setContent(PAGE_LEFT);
                    setContent(PAGE_RIGHT);
                }
            }
        });

        return view;
    }

    private void setContent(int index) {
        final PageModel model =  mPageModel[index];
        //model.textView.setText(model.getText());

        model.kalenderAdapter.clear();

        model.kalenderAdapter = new KalenderAdapter(model.context);
        model.recyclerView.setHasFixedSize(false);

        model.mLayoutManager = new LinearLayoutManager(model.context);
        model.recyclerView.setLayoutManager(model.mLayoutManager);

        model.recyclerView.setAdapter(model.kalenderAdapter);


        for(int i = 7; i <= 23; i++) {
            AgendaItem agendaItem = new AgendaItem();
            agendaItem.setHour(i);
            model.kalenderAdapter.addItem(model.kalenderAdapter.getItemCount(), agendaItem);
        }

        List<Afspraak> afspraken = db.getAfsprakenForDate(model.getDatum("yyyy-MM-dd"));
        for(Afspraak afspraak : afspraken) {
            Calendar begin = Calendar.getInstance();
            begin.setTime(afspraak.getStart());

            Calendar end = Calendar.getInstance();
            end.setTime(afspraak.getEnd());

            int beginhour = begin.get(Calendar.HOUR_OF_DAY);
            int endhour = end.get(Calendar.HOUR_OF_DAY);

            for(int a = 0; a < endhour - beginhour; a++) {
                AgendaItem kli = (AgendaItem) model.kalenderAdapter.getItemByHour(beginhour + 1 + a);
                kli.addAfspraak(afspraak);
                model.kalenderAdapter.updateItem(kli);
            }
        }

        model.kalenderDatum.setText(model.getDatum("dd MMMM yyyy"));
    }

    private void initPageModel() {
        for (int i = 0; i < mPageModel.length; i++) {
            // initing the pagemodel with indexes of -1, 0 and 1
            mPageModel[i] = new PageModel(i - 1);
        }
    }

    private class MyagerAdaper extends PagerAdapter {

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            // we only need three pages
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PageModel currentPage = mPageModel[position];
            currentPage.layout = (LinearLayout) mInflater.inflate(R.layout.activity_kalender, null);
            currentPage.recyclerView = (RecyclerView) currentPage.layout.findViewById(R.id.kalenderlist);
            currentPage.kalenderDatum = (TextView) currentPage.layout.findViewById(R.id.kalenderdatum);
            //textView.setText(currentPage.getText());

            currentPage.context = container.getContext();


            currentPage.kalenderAdapter = new KalenderAdapter(container.getContext());
            currentPage.recyclerView.setHasFixedSize(false);

            currentPage.mLayoutManager = new LinearLayoutManager(container.getContext());
            currentPage.recyclerView.setLayoutManager(currentPage.mLayoutManager);

            currentPage.recyclerView.setAdapter(currentPage.kalenderAdapter);


            for(int i = 7; i <= 23; i++) {
                AgendaItem agendaItem = new AgendaItem();
                agendaItem.setHour(i);
                currentPage.kalenderAdapter.addItem(currentPage.kalenderAdapter.getItemCount(), agendaItem);
            }

            currentPage.kalenderDatum.setText(currentPage.getDatum("dd MMMM yyyy"));

            container.addView(currentPage.layout);


            return currentPage.layout;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    @Override
    public void onStart() {
        mainActivity.setupActionBar();
        super.onStart();
    }

    //Setup broadcast listener
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("DATA_UPDATE"));

        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void unload() {
        toolbar.findViewById(R.id.action_refresh).clearAnimation();
        toolbar.removeView(toolbar.findViewById(R.id.action_refresh));
    }

    public void registerToolbarClick() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_refresh:
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public int getDrawerId() {
        return R.id.menu_calendar;
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.calendar);
    }
}
