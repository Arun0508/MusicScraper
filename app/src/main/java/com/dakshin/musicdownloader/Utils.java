package com.dakshin.musicdownloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.ByteArrayOutputStream;
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
    static String starmusiq="starmusiq";
    static String songslover="songslover";
    static String starmusiq_activity="starmusiq_activity";
    static String currentSite=starmusiq; //holds which site is being searched
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
        return getResizedBitmap(result[0],pxFromDp(100),pxFromDp(100));
    }
    private static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        if (bm == null)
            return bm;
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP

        return Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
    }
    private static int pxFromDp(int dp)
    {
        return dp *(int)density;
    }

    private static int dpFromPx(int px)
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
                    callback.onSongsloverDownloadComplete(file);

                } catch (IOException e) {
                    Log.e("tag","ioexception in download queue");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**Download response headers from songslover does not contain a content-disposition. So name
     * has to be passed to the func manually
     * @param url the url of an mp3 file
     * @param callback a DownloadCompleteListener is triggered when the file is ready
     */
    Thread downloadFromURL_no_headers(final String url,final DownloadCompleteListener callback) {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL _url=new URL(url);
                    URLConnection connection=_url.openConnection();
                    InputStream input = connection.getInputStream();
                    byte[] buffer = new byte[4096];
                    int n;
                    File file=new File(Environment.getExternalStorageDirectory(),
                            "temp"+System.currentTimeMillis()+".mp3");
                    OutputStream output = new FileOutputStream(file);
                    while ((n = input.read(buffer)) != -1)
                    {
                        output.write(buffer, 0, n);
                    }
                    output.close();
                    callback.onSongsloverDownloadComplete(file);

                } catch (IOException e) {
                    Log.e("tag","ioexception in download queue");
                    e.printStackTrace();
                }
            }
        });
        t.start();
        return t;
    }

    void unzip(File zipFile) {
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
    Thread downloadToByteArray(final String iconUrl,final ByteArrayDownloadListener callback) {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (iconUrl.equals(""))
                        callback.onByteArrayDownloadComplete(null);
                    URL url = new URL(iconUrl);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (InputStream is = url.openStream()) {
                        byte[] byteChunk = new byte[4096];
                        int n;

                        while ((n = is.read(byteChunk)) > 0) {
                            baos.write(byteChunk, 0, n);
                        }
                    } catch (IOException e) {
                        System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
                        e.printStackTrace();
                    }
                    callback.onByteArrayDownloadComplete(baos.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        return t;
    }
}
