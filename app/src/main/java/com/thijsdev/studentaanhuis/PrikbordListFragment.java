package com.thijsdev.studentaanhuis;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PrikbordListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private PrikbordAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prikbord_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.prikbordList);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST));

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PrikbordAdapter(this.getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public PrikbordAdapter getPrikbordAdapter() {
        return mAdapter;
    }
}
