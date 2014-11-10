package com.jbirdvegas.remotecodeexecution;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import dalvik.system.DexClassLoader;

/**
 * Modeled after:
 * https://github.com/simpleton/AndroidDynamicLoader/blob/master/FragmentLoader/src/com/dianping/example/fragmentloader/ListApkFragment.java
 * &&
 * https://enplug.com/blog/loading-classes-dynamically-on-android-particularly-when-you-have-to-use-them-in-a-third-party-framework-like-libgdx
 */
public class FragmentLoader extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Don't reload on configuration changes
        if (savedInstanceState != null) {
            return;
        }
        FrameLayout rootView = new FrameLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setId(android.R.id.primary);
        setContentView(rootView);

        if (Constants.ACTION_LAUNCH_FRAGMENT.equals(getIntent().getAction())) {
            try {
                String fragmentClass = getIntent().getStringExtra(Constants.EXTRA_CLASS_TO_LOAD);
                String pathToApk = getIntent().getStringExtra(Constants.EXTRA_PATH_TO_FILE);
                Fragment fragment = (Fragment) getClassFromApk(fragmentClass, pathToApk).newInstance();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(android.R.id.primary, fragment);
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Fragment f = new ErrorFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.primary, f);
            ft.commit();
        }
    }

    /**
     *
     * @param fullClassName fully qualified name of the class you want to load e.g. "com.enplug.games.PhotoWall";
     * @param fullPathToApk full path to the apk or jar containing classes.dex with that class definition
     * @return Class found by the at the specified path
     * @throws ClassNotFoundException if the resolved dex does not contain the Class specified
     */
    public Class<?> getClassFromApk(String fullClassName, String fullPathToApk) throws ClassNotFoundException {
        String dexOutputDir = getApplicationContext().getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
        // Get the current class loader and pass it as parent when creating DexClassLoader
        DexClassLoader dexLoader = new DexClassLoader(fullPathToApk,
                dexOutputDir,
                null,
                getClassLoader());
        // Use dex loader to load the class
        return Class.forName(fullClassName, true, dexLoader);
    }
}
