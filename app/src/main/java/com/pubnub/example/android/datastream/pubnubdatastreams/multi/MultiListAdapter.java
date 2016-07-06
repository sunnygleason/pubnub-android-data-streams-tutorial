package com.pubnub.example.android.datastream.pubnubdatastreams.multi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pubnub.example.android.datastream.pubnubdatastreams.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiListAdapter extends ArrayAdapter<MultiPojo> {
    private final Context context;
    private final LayoutInflater inflater;

    private final List<String> multiList = new ArrayList<String>();
    private final Map<String, MultiPojo> latestMultiMessage = new LinkedHashMap<String, MultiPojo>();

    public MultiListAdapter(Context context) {
        super(context, R.layout.list_row_multi);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void add(MultiPojo message) {
        if (latestMultiMessage.containsKey(message.getChannel())) {
            this.multiList.remove(message.getChannel());
        }

        this.multiList.add(0, message.getChannel());
        latestMultiMessage.put(message.getChannel(), message);

        ((Activity) this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String channel = this.multiList.get(position);

        MultiPojo multiMsg = this.latestMultiMessage.get(channel);
        MultiListRowUi msgView;

        if (convertView == null) {
            msgView = new MultiListRowUi();

            convertView = inflater.inflate(R.layout.list_row_multi, parent, false);

            msgView.channel = (TextView) convertView.findViewById(R.id.channel);
            msgView.sender = (TextView) convertView.findViewById(R.id.sender);
            msgView.message = (TextView) convertView.findViewById(R.id.message);
            msgView.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

            convertView.setTag(msgView);
        } else {
            msgView = (MultiListRowUi) convertView.getTag();
        }

        msgView.channel.setText(multiMsg.getChannel());
        msgView.sender.setText(multiMsg.getSender());
        msgView.message.setText(multiMsg.getMessage());
        msgView.timestamp.setText(multiMsg.getTimestamp());

        return convertView;
    }

    @Override
    public int getCount() {
        return this.multiList.size();
    }

    public void clear() {
        this.multiList.clear();
        this.latestMultiMessage.clear();
        notifyDataSetChanged();
    }
}
