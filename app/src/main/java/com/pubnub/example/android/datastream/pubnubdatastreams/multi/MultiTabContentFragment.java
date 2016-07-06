package com.pubnub.example.android.datastream.pubnubdatastreams.multi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pubnub.example.android.datastream.pubnubdatastreams.R;

public class MultiTabContentFragment extends Fragment {
    private MultiListAdapter mlAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multi, container, false);
        ListView listView = (ListView) view.findViewById(R.id.multi_list);
        listView.setAdapter(mlAdapter);

        return view;
    }

    public void setAdapter(MultiListAdapter mlAdapter) {
        this.mlAdapter = mlAdapter;
    }
}
