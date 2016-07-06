package com.pubnub.example.android.datastream.pubnubdatastreams;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.common.collect.ImmutableMap;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.example.android.datastream.pubnubdatastreams.multi.MultiListAdapter;
import com.pubnub.example.android.datastream.pubnubdatastreams.multi.MultiPnCallback;
import com.pubnub.example.android.datastream.pubnubdatastreams.presence.PresenceListAdapter;
import com.pubnub.example.android.datastream.pubnubdatastreams.presence.PresencePnCallback;
import com.pubnub.example.android.datastream.pubnubdatastreams.presence.PresencePojo;
import com.pubnub.example.android.datastream.pubnubdatastreams.pubsub.PubSubListAdapter;
import com.pubnub.example.android.datastream.pubnubdatastreams.pubsub.PubSubPnCallback;
import com.pubnub.example.android.datastream.pubnubdatastreams.util.DateTimeUtil;
import com.pubnub.example.android.datastream.pubnubdatastreams.util.JsonUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final List<String> MULTI_CHANNELS = Arrays.asList(Constants.MULTI_CHANNEL_NAMES.split(","));
    public static final List<String> PUBSUB_CHANNEL = Arrays.asList(Constants.CHANNEL_NAME);

    private ScheduledExecutorService mScheduleTaskExecutor;

    private PubNub mPubnub_DataStream;
    private PubSubListAdapter mPubSub;
    private PubSubPnCallback mPubSubPnCallback;

    private PresenceListAdapter mPresence;
    private PresencePnCallback mPresencePnCallback;

    private PubNub mPubnub_Multi;
    private MultiListAdapter mMulti;
    private MultiPnCallback mMultiPnCallback;

    private SharedPreferences mSharedPrefs;
    private String mUsername;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE);
        if (!mSharedPrefs.contains(Constants.DATASTREAM_UUID)) {
            Intent toLogin = new Intent(this, LoginActivity.class);
            startActivity(toLogin);
            return;
        }

        this.mUsername = mSharedPrefs.getString(Constants.DATASTREAM_UUID, "");
        this.mPubSub = new PubSubListAdapter(this);
        this.mPresence = new PresenceListAdapter(this);
        this.mMulti = new MultiListAdapter(this);

        this.mPubSubPnCallback = new PubSubPnCallback(this.mPubSub);
        this.mPresencePnCallback = new PresencePnCallback(this.mPresence);
        this.mMultiPnCallback = new MultiPnCallback(this.mMulti);

        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Pub/Sub"));
        tabLayout.addTab(tabLayout.newTab().setText("Presence"));
        tabLayout.addTab(tabLayout.newTab().setText("Multi"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final MainActivityTabManager adapter = new MainActivityTabManager
                (getSupportFragmentManager(), tabLayout.getTabCount());

        adapter.setPubSubAdapter(this.mPubSub);
        adapter.setPresenceAdapter(this.mPresence);
        adapter.setMultiAdapter(this.mMulti);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        initPubNub();
        initChannels();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        disconnectAndCleanup();

        Intent toLogin = new Intent(this, LoginActivity.class);
        startActivity(toLogin);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectAndCleanup();
    }

    public void publish(View view) {
        final EditText mMessage = (EditText) MainActivity.this.findViewById(R.id.new_message);

        final Map<String, String> message = ImmutableMap.<String, String>of("sender", MainActivity.this.mUsername, "message", mMessage.getText().toString(), "timestamp", DateTimeUtil.getTimeStampUtc());

        MainActivity.this.mPubnub_DataStream.publish().channel(Constants.CHANNEL_NAME).message(message).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        try {
                            if (!status.isError()) {
                                mMessage.setText("");
                                Log.v(TAG, "publish(" + JsonUtil.asJson(result) + ")");
                            } else {
                                Log.v(TAG, "publishErr(" + JsonUtil.asJson(status) + ")");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    private final void initPubNub() {
        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        config.setUuid(this.mUsername);
        config.setSecure(true);

        this.mPubnub_DataStream = new PubNub(config);
        this.mPubnub_Multi = new PubNub(config);
    }

    private final void initChannels() {
        this.mPubnub_DataStream.addListener(this.mPubSubPnCallback);
        this.mPubnub_DataStream.addListener(this.mPresencePnCallback);

        this.mPubnub_DataStream.subscribe().channels(PUBSUB_CHANNEL).withPresence().execute();
        this.mPubnub_DataStream.hereNow().channels(PUBSUB_CHANNEL).async(new PNCallback<PNHereNowResult>() {
            @Override
            public void onResponse(PNHereNowResult result, PNStatus status) {
                if (status.isError()) {
                    return;
                }

                try {
                    Log.v(TAG, JsonUtil.asJson(result));

                    for (Map.Entry<String, PNHereNowChannelData> entry : result.getChannels().entrySet()) {
                        for (PNHereNowOccupantData occupant : entry.getValue().getOccupants()) {
                            MainActivity.this.mPresence.add(new PresencePojo(occupant.getUuid(), "join", DateTimeUtil.getTimeStampUtc()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.mPubnub_Multi.addListener(mMultiPnCallback);
        this.mPubnub_Multi.subscribe().channels(MULTI_CHANNELS).execute();


        this.mScheduleTaskExecutor = Executors.newScheduledThreadPool(1);
        this.mScheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int randomIndex = random.nextInt(MULTI_CHANNELS.size());
                        String toChannel = MULTI_CHANNELS.get(randomIndex);

                        final Map<String, String> message = ImmutableMap.<String, String>of("sender", MainActivity.this.mUsername, "message", "randomly fired on this channel", "timestamp", DateTimeUtil.getTimeStampUtc());

                        MainActivity.this.mPubnub_Multi.publish().channel(toChannel).message(message).async(
                                new PNCallback<PNPublishResult>() {
                                    @Override
                                    public void onResponse(PNPublishResult result, PNStatus status) {
                                        try {
                                            if (!status.isError()) {
                                                Log.v(TAG, "publish(" + JsonUtil.asJson(result) + ")");
                                            } else {
                                                Log.v(TAG, "publishErr(" + JsonUtil.asJson(status) + ")");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        );
                    }
                });

            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    private void disconnectAndCleanup() {
        getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE).edit().clear().commit();

        if (this.mPubnub_DataStream != null) {
            this.mPubnub_DataStream.unsubscribe().channels(PUBSUB_CHANNEL).execute();
            this.mPubnub_DataStream.removeListener(this.mPubSubPnCallback);
            this.mPubnub_DataStream.removeListener(this.mPresencePnCallback);
            this.mPubnub_DataStream.stop();
            this.mPubnub_DataStream = null;
        }

        if (this.mPubnub_Multi != null) {
            this.mPubnub_Multi.unsubscribe().channels(MULTI_CHANNELS).execute();
            this.mPubnub_Multi.removeListener(this.mMultiPnCallback);
            this.mPubnub_Multi.stop();
            this.mPubnub_Multi = null;
        }

        if (this.mScheduleTaskExecutor != null) {
            this.mScheduleTaskExecutor.shutdownNow();
            this.mScheduleTaskExecutor = null;
        }
    }
}
