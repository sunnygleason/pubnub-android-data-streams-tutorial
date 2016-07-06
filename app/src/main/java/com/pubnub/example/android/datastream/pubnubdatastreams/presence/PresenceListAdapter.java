package com.pubnub.example.android.datastream.pubnubdatastreams.presence;

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

public class PresenceListAdapter extends ArrayAdapter<PresencePojo> {
    private final Context context;
    private final LayoutInflater inflater;

    private final List<String> presenceList = new ArrayList<String>();
    private final Map<String, PresencePojo> latestPresence = new LinkedHashMap<String, PresencePojo>();

    public PresenceListAdapter(Context context) {
        super(context, R.layout.list_row_presence);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void add(PresencePojo message) {
        if (latestPresence.containsKey(message.getSender())) {
            this.presenceList.remove(message.getSender());
        }

        this.presenceList.add(0, message.getSender());
        latestPresence.put(message.getSender(), message);

        ((Activity) this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String sender = this.presenceList.get(position);
        PresencePojo presenceMsg = this.latestPresence.get(sender);

        PresenceMessageListRowUi msgView;

        if (convertView == null) {
            msgView = new PresenceMessageListRowUi();

            convertView = inflater.inflate(R.layout.list_row_presence, parent, false);

            msgView.sender = (TextView) convertView.findViewById(R.id.sender);
            msgView.presence = (TextView) convertView.findViewById(R.id.value);
            msgView.timestamp = (TextView) convertView.findViewById(R.id.timestamp);

            convertView.setTag(msgView);
        } else {
            msgView = (PresenceMessageListRowUi) convertView.getTag();
        }

        msgView.sender.setText(presenceMsg.getSender());
        msgView.presence.setText(presenceMsg.getPresence());
        msgView.timestamp.setText(presenceMsg.getTimestamp());

        return convertView;
    }

    @Override
    public int getCount() {
        return this.presenceList.size();
    }

    public void clear() {
        this.presenceList.clear();
        this.latestPresence.clear();
        notifyDataSetChanged();
    }
}
