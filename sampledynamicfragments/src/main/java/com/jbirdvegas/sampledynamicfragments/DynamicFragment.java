package com.jbirdvegas.sampledynamicfragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Authored by jbird on 11/9/14.
 */
public class DynamicFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Toast.makeText(getActivity(), "This was downloaded from the web!!!", Toast.LENGTH_LONG).show();
        rootView.setBackgroundResource(R.drawable.bird);
        new ImageDownloader(rootView).execute("https://avatars1.githubusercontent.com/u/665317?v=3&s=120");
        return rootView;
    }
    class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        View view;

        public ImageDownloader(View bmImage) {
            this.view = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            view.setBackground(new BitmapDrawable(result));
        }
    }
}
