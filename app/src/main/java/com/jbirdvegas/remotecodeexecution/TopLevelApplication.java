package com.jbirdvegas.remotecodeexecution;

import android.app.Application;

/**
 * Authored by jbird on 11/9/14.
 */
public class TopLevelApplication extends Application {
    private static TopLevelApplication mInstance;

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static TopLevelApplication getApplication() {
        return mInstance;
    }
}
