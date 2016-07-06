package com.pubnub.example.android.datastream.pubnubdatastreams.presence;

import android.util.Log;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.example.android.datastream.pubnubdatastreams.util.DateTimeUtil;
import com.pubnub.example.android.datastream.pubnubdatastreams.util.JsonUtil;

public class PresencePnCallback extends SubscribeCallback {
    private static final String TAG = PresencePnCallback.class.getName();
    private final PresenceListAdapter presenceListAdapter;

    public PresencePnCallback(PresenceListAdapter presenceListAdapter) {
        this.presenceListAdapter = presenceListAdapter;
    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        // no status handling for simplicity
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        // no message handling for simplicity
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        try {
            Log.v(TAG, "presenceP(" + JsonUtil.asJson(presence) + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sender = presence.getUuid();
        String presenceString = presence.getEvent().toString();
        String timestamp = DateTimeUtil.getTimeStampUtc();

        PresencePojo pm = new PresencePojo(sender, presenceString, timestamp);
        presenceListAdapter.add(pm);
    }
}
