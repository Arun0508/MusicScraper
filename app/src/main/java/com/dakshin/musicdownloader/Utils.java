package com.dakshin.musicdownloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

class Utils {
    public static float density;
    static Bitmap getBitmapFromURL(final String src) {
        final Bitmap[] result = {null};
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    java.net.URL url = new java.net.URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    result[0] = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getResizedBitmap(result[0],100,100);
    }
    private static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }
    private int pxFromDp(int dp)
    {
        return dp *(int)density;
    }

    private int dpFromPx(int px)
    {
        return px /(int)density;
    }

    void downloadFromURL(final DownloadCompleteListener callback, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL _url=new URL(url);
                    URLConnection connection=_url.openConnection();
                    Map<String, List<String>> map = connection.getHeaderFields();

                    //get header by 'key'
                    String filename = connection.getHeaderField("Content-Disposition").split("filename=")[1];
                    //remove the quotes around the filename
                    filename=filename.substring(1,filename.length()-1);
                    InputStream input = connection.getInputStream();
                    byte[] buffer = new byte[4096];
                    int n;
                    File file=new File(Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_MUSIC),filename);
                    OutputStream output = new FileOutputStream(file);
                    while ((n = input.read(buffer)) != -1)
                    {
                        output.write(buffer, 0, n);
                    }
                    output.close();
                    //todo: send some kind of callback to StarmusiqAlbum
                    callback.onDownloadComplete(file);

                } catch (IOException e) {
                    Log.e("tag","ioexception in download queue");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void unzip(File zipFile) {
        String unzipDirectory=zipFile.getParentFile().getAbsolutePath();
        try {
            Log.d("tag","starting unzip");
            ZipFile zipFile1=new ZipFile(zipFile.getAbsolutePath());
            zipFile1.extractAll(unzipDirectory);
            Log.d("tag","finished unzip");
            zipFile.delete();
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

}
