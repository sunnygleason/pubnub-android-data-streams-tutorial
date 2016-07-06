package com.pubnub.example.android.datastream.pubnubdatastreams.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class MultiPojo {
    private final String channel;
    private final String sender;
    private final String message;
    private final String timestamp;

    public MultiPojo(@JsonProperty("channel") String channel, @JsonProperty("sender") String sender, @JsonProperty("message") String message, @JsonProperty("timestamp") String timestamp) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getChannel() {
        return channel;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MultiPojo other = (MultiPojo) obj;

        return Objects.equal(this.channel, other.channel)
                && Objects.equal(this.sender, other.sender)
                && Objects.equal(this.message, other.message)
                && Objects.equal(this.timestamp, other.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(channel, sender, message, timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(MultiPojo.class)
                .add("channel", channel)
                .add("sender", sender)
                .add("message", message)
                .add("timestamp", timestamp)
                .toString();
    }
}
