package org.sandec.wisatasmg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.adapter.WisataAdapter;
import org.sandec.wisatasmg.database.DatabaseHelper;
import org.sandec.wisatasmg.model.WisataModel;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {


    public FavoriteFragment() {
        // Required empty public constructor
    }
    //kenalin
    RecyclerView recyclerView;
    ArrayList<WisataModel> listData;
    DatabaseHelper database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        //hubungin
        recyclerView = (RecyclerView) view.findViewById(R.id.recyler_view);

        //Data
        listData = new ArrayList<>();

        ambilData();

        //adapter
        WisataAdapter adapter = new WisataAdapter(listData, getActivity());
        recyclerView.setAdapter(adapter);

        // layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private void ambilData() {
        database = new DatabaseHelper(getActivity());
        listData = database.getDataFavorite();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
