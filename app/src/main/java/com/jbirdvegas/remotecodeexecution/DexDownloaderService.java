package com.jbirdvegas.remotecodeexecution;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DexDownloaderService extends IntentService {
    private static final String TAG = DexDownloaderService.class.getSimpleName();

    public DexDownloaderService() {
        super(DexDownloaderService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String url = intent.getStringExtra(Constants.EXTRA_URL);
            sendUpdate("Starting download...");
            try {
                File file = new File(getExternalCacheDir(), "dynamic.apk");
                new DefaultHttpClient().execute(new HttpGet(url))
                        .getEntity().writeTo(new FileOutputStream(file));
                sendUpdate("Download complete {" + file.length() + "}... launching frag");
                sendLaunchFrag(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                sendUpdate("Error: " + e.getMessage());
            }
        }
    }

    private void sendLaunchFrag(String absolutePath) {
        Log.d(TAG, "Sending path: " + absolutePath);
        Log.d(TAG, "Exists? " + new File(absolutePath).exists());
        Intent intent = new Intent(Constants.ACTION_LAUNCH_FRAGMENT);
        intent.putExtra(Constants.EXTRA_PATH_TO_FILE, absolutePath);
        getPath();
        // TODO: We could pull the class name from a config on server somewhere
        intent.putExtra(Constants.EXTRA_CLASS_TO_LOAD, "com.jbirdvegas.sampledynamicfragments.DynamicFragment");
        sendBroadcast(intent);
    }

    private void getPath() {
        try {
            AssetManager asset = getApplicationContext().getAssets();
            String[] apkses = asset.list("apks");
            Log.d(TAG, "Apk assets: " + Arrays.toString(apkses));
            for (String s : apkses) {
//                addItem(s, "apks/" + s);
                Log.d(TAG, String.format("name {%s} path {%s}", s, "apks/ + s"));
            }
        } catch (Exception e) {
        }
    }

    private void sendUpdate(String status) {
        Log.d(TAG, "Status update: " + status);
        Intent intent = new Intent(Constants.ACTION_TEXT_UPDATE);
        intent.putExtra(Constants.EXTRA_STATUS, status);
        sendBroadcast(intent);
    }
}
