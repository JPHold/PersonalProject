package com.hjp.projectone.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.hjp.projectone.BaseContent.CallListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HJP on 2016/7/12 0012.
 */

public class PhotoAsyncTask extends AsyncTask<String, Integer, String> {
    private static final String SUCCESSTAG = "ok";
    private static final String ERROTTAG = "no";

    private final ImageView mImageView;
    private final int mId;
    private final int currId;
    private final CallListener mCallListener;
    private Bitmap bitmap;

    public PhotoAsyncTask(ImageView view, int id, CallListener callListener) {
        mImageView = view;
        mId = mImageView.getId();
        currId = id;
        mCallListener = callListener;
    }

    @Override
    protected String doInBackground(String... params) {
        if (currId == mId) {
            String photoUri = params[0];
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(photoUri);

                try {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = httpURLConnection.getResponseCode();

                    if (responseCode == 200) {
                        inputStream = httpURLConnection.getInputStream();
                        if (inputStream != null) {
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            return SUCCESSTAG;
                        }
                    }
                } catch (IOException e) {
                    return ERROTTAG;
                }
            } catch (MalformedURLException e) {
                return ERROTTAG;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ERROTTAG;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (SUCCESSTAG.equals(s)) {
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
                if (mCallListener != null)
                    mCallListener.call("ok");
            }
        }
    }
}
