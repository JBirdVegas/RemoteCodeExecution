package com.jbirdvegas.sampledynamicfragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URL;

/**
 * TODO: Resources can't be trusted.  When loading classes from downloaded dex. Android will attempt
 * to load R.* references from the parent application's classpath.  This will fail but not with a
 * Runtime exception. Instead it appears Androd just selects a resource from the "platform" apk,
 * ie the apk that initialized the download.
 * <p/>
 * TODO: Attempt to load R class resources from downloaded apk to eliminate this problem
 */
public class DynamicFragment extends Fragment {
    private static final String DOWNLOAD_URL = "https://avatars1.githubusercontent.com/u/665317?s=960";
    private FrameLayout mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = new FrameLayout(getActivity());
        mRootView.setBackgroundColor(getResources().getColor(android.R.color.black));
        ImageView imageView = new ImageView(getActivity());
        mRootView.addView(imageView);
        new ImageDownloader(imageView).execute();
        return mRootView;
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        private final ImageView mImageView;

        public ImageDownloader(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "This was downloaded from the web!!!",
                    Toast.LENGTH_LONG).show();
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap icon = null;
            try {
                icon = BitmapFactory.decodeStream(new URL(DOWNLOAD_URL).openStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(result);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);
            mImageView.setLayoutParams(params);
        }
    }
}