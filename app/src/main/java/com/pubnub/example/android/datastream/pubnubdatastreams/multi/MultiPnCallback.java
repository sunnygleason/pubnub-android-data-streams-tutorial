package com.pubnub.example.android.datastream.pubnubdatastreams.multi;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.example.android.datastream.pubnubdatastreams.util.JsonUtil;

import java.util.LinkedHashMap;

public class MultiPnCallback extends SubscribeCallback {
    private static final String TAG = MultiPnCallback.class.getName();
    private final MultiListAdapter multiListAdapter;

    public MultiPnCallback(MultiListAdapter multiListAdapter) {
        this.multiListAdapter = multiListAdapter;
    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        /*
        switch (status.getCategory()) {
             // for common cases to handle, see: https://www.pubnub.com/docs/java/pubnub-java-sdk-v4
             case PNStatusCategory.PNConnectedCategory:
             case PNStatusCategory.PNUnexpectedDisconnectCategory:
             case PNStatusCategory.PNReconnectedCategory:
             case PNStatusCategory.PNDecryptionErrorCategory:
         }
        */

        // no presence handling for simplicity
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        try {
            Log.v(TAG, "multi(" + JsonUtil.asJson(message) + ")");

            JsonNode jsonMsg = message.getMessage();

            LinkedHashMap<String, Object> initial = new LinkedHashMap<String, Object>();
            initial.put("channel", message.getSubscribedChannel());
            initial.putAll(JsonUtil.convert(jsonMsg, LinkedHashMap.class));

            MultiPojo mlMsg = JsonUtil.convert(initial, MultiPojo.class);
            this.multiListAdapter.add(mlMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        // no presence handling for simplicity
    }
}
