package com.jbirdvegas.remotecodeexecution;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    private static final String SOME_URL = "https://github.com/JBirdVegas/tests/raw/master/sampledynamicfragments-debug.apk";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mStatus;
    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                Log.d(TAG, "Received action: " + action);
                switch (action) {
                    case Constants.ACTION_TEXT_UPDATE:
                        String status = intent.getStringExtra(Constants.EXTRA_STATUS);
                        if (status != null) {
                            mStatus.setText(status);
                        }
                        break;
                    case Constants.ACTION_LAUNCH_FRAGMENT:
                        String clazz = intent.getStringExtra(Constants.EXTRA_CLASS_TO_LOAD);
                        String clazzLoggingMessage = "Launching... " + clazz;
                        Log.d(TAG, clazzLoggingMessage);
                        mStatus.setText(clazzLoggingMessage);
                        Intent launch = new Intent(getApplicationContext(), FragmentLoader.class);
                        launch.setAction(Constants.ACTION_LAUNCH_FRAGMENT);
                        launch.putExtras(intent);
                        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(launch);
                        break;
                    default:
                        mStatus.setText("Unexpected action: " + action);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatus = (TextView) findViewById(R.id.status_textview);
        findViewById(R.id.init_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent downloader = new Intent(getApplicationContext(), DexDownloaderService.class);
                downloader.putExtra(Constants.EXTRA_URL, SOME_URL);
                startService(downloader);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mStatusReceiver, getActionsFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mStatusReceiver);
    }

    private IntentFilter getActionsFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_LAUNCH_FRAGMENT);
        filter.addAction(Constants.ACTION_TEXT_UPDATE);
        return filter;
    }
}