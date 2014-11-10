package com.jbirdvegas.remotecodeexecution;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;

import dalvik.system.DexClassLoader;

public class FragmentLoader extends Activity {
    private static final String TAG = FragmentLoader.class.getSimpleName();
    private AssetManager mAssetManager;
    private Resources mResources;
    private Theme mTheme;
    private ClassLoader mClassloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Constants.ACTION_LAUNCH_FRAGMENT.equals(getIntent().getAction())) {
            // we need to setup environment before super.onCreate
            try {
                String path = getIntent().getStringExtra(Constants.EXTRA_CLASS_TO_LOAD);
                Log.d(TAG, "Received path to load: " + path);
//                InputStream inputStream = TopLevelApplication.getApplication().getAssets().open(path);
//                byte[] bytes = new byte[inputStream.available()];
//                inputStream.read(bytes);
//                inputStream.close();
//
//                File dex = new File(TopLevelApplication.getApplication().getFilesDir(), "dex");
//                dex.mkdir();
//                String outPath = String.format("FL_%s.apk", Integer.toHexString(path.hashCode()));
//                dex = new File(dex, outPath);
//                FileOutputStream fos = new FileOutputStream(dex);
//                fos.write(bytes);
//                fos.close();
//
//                File dexout = new File(TopLevelApplication.getApplication().getFilesDir(), "dexout");
//                dexout.mkdir();
//
//                mClassloader = new DexClassLoader(dex.getAbsolutePath(),
//                        dexout.getAbsolutePath(), null, super.getClassLoader());
//
//                try {
//                    mAssetManager = AssetManager.class.newInstance();
//                    mAssetManager.getClass()
//                            .getMethod("addAssetPath", String.class)
//                            .invoke(mAssetManager, dex.getAbsolutePath());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//                mResources = new Resources(mAssetManager,
//                        super.getResources().getDisplayMetrics(),
//                        super.getResources().getConfiguration());
//
//                mTheme = mResources.newTheme();
//                mTheme.setTo(super.getTheme());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onCreate(savedInstanceState);

        FrameLayout rootView = new FrameLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setId(android.R.id.primary);
        setContentView(rootView);

        if (savedInstanceState != null)
            return;

        if (Constants.ACTION_LAUNCH_FRAGMENT.equals(getIntent().getAction())) {
            try {
                String fragmentClass = getIntent().getStringExtra(Constants.EXTRA_CLASS_TO_LOAD);
                String pathToApk = getIntent().getStringExtra(Constants.EXTRA_PATH_TO_FILE);
//                Fragment f = (Fragment) getClassLoader().loadClass(fragmentClass).newInstance();
                File apk = new File(pathToApk);
                Log.d(TAG, "Found path exists? " + apk.exists());
                Fragment fragment = (Fragment) loadClassDynamically(fragmentClass, pathToApk).newInstance();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(android.R.id.primary, fragment);
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Fragment f = new PlaceHolderFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.primary, f);
            ft.commit();
        }
    }

    // fullClassName is the fully qualified name of the class you want to load e.g. "com.enplug.games.PhotoWall";
// fullPathToApk is the full path to the apk or jar containing classes.dex with that class definition
//
    public Class<?> loadClassDynamically(String fullClassName, String fullPathToApk) throws ClassNotFoundException {
        File dexOutputDir = getApplicationContext().getDir("dex", Context.MODE_PRIVATE);

        // Get the current class loader and pass it as parent when creating DexClassLoader
        //
        DexClassLoader dexLoader = new DexClassLoader(fullPathToApk,
                dexOutputDir.getAbsolutePath(),
                null,
                getClassLoader());
        // Use dex loader to load the class
        //
        Class<?> loadedClass = Class.forName(fullClassName, true, dexLoader);
        return loadedClass;
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }

    @Override
    public ClassLoader getClassLoader() {
        return mClassloader == null ? super.getClassLoader() : mClassloader;
    }
}
