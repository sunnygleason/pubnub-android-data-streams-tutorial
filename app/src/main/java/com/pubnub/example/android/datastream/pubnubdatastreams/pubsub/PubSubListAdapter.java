package com.pubnub.example.android.datastream.pubnubdatastreams.pubsub;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pubnub.example.android.datastream.pubnubdatastreams.R;

import java.util.ArrayList;
import java.util.List;

public class PubSubListAdapter extends ArrayAdapter<PubSubPojo> {
    private final Context context;
    private final LayoutInflater inflater;
    private final List<PubSubPojo> values = new ArrayList<PubSubPojo>();

    public PubSubListAdapter(Context context) {
        super(context, R.layout.list_row_pubsub);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void add(PubSubPojo message) {
        this.values.add(0, message);

        ((Activity) this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PubSubPojo dsMsg = this.values.get(position);
        PubSubListRowUi msgView;

        if (convertView == null) {
            msgView = new PubSubListRowUi();

            convertView = inflater.inflate(R.layout.list_row_pubsub, parent, false);

            msgView.sender = (TextView) convertView.findViewById(R.id.sender);
            msgView.message = (TextView) convertView.findViewById(R.id.message);
            msgView.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

            convertView.setTag(msgView);
        } else {
            msgView = (PubSubListRowUi) convertView.getTag();
        }

        msgView.sender.setText(dsMsg.getSender());
        msgView.message.setText(dsMsg.getMessage());
        msgView.timestamp.setText(dsMsg.getTimestamp());

        return convertView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    public void clear() {
        this.values.clear();
        notifyDataSetChanged();
    }
}
