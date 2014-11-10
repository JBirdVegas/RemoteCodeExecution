package com.jbirdvegas.remotecodeexecution;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DexDownloaderService extends IntentService {
    private static final String TAG = DexDownloaderService.class.getSimpleName();
    private static final String DYNAMIC_FRAGMENT_CLASSPATH = "com.jbirdvegas.sampledynamicfragments.DynamicFragment";

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
                downloadFile(url, file);
                sendUpdate(String.format("Download complete bytes:{%d}... launching Fragment", file.length()));
                sendLaunchFrag(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                sendUpdate(String.format("Error: %s", e.getMessage()));
            }
        }
    }

    private void downloadFile(String url, File file) throws IOException {
        new DefaultHttpClient().execute(new HttpGet(url)).getEntity().writeTo(new FileOutputStream(file));
    }

    private void sendLaunchFrag(String absolutePath) {
        Log.d(TAG, String.format("Sending path: %s exists? %s", absolutePath, new File(absolutePath).exists()));
        Intent intent = new Intent(Constants.ACTION_LAUNCH_FRAGMENT);
        intent.putExtra(Constants.EXTRA_PATH_TO_FILE, absolutePath);
        // TODO: We could pull the class name from a config on server somewhere
        intent.putExtra(Constants.EXTRA_CLASS_TO_LOAD, DYNAMIC_FRAGMENT_CLASSPATH);
        sendBroadcast(intent);
    }

    private void sendUpdate(String status) {
        Log.d(TAG, String.format("Status update: %s", status));
        Intent intent = new Intent(Constants.ACTION_TEXT_UPDATE);
        intent.putExtra(Constants.EXTRA_STATUS, status);
        sendBroadcast(intent);
    }
}