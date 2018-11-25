package com.dakshin.musicdownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v22Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
SongsLoverListener,DownloadCompleteListener,ByteArrayDownloadListener{
    ImageView imageView;
    TextView textView1, textView2,textView3;
    SearchResultMenuItem menuItem;
    private Context context;
    private byte[] icon; //used to hold album art for songslover
    private File songFile;
    private String songName;

    public SearchResultViewHolder(Context context, View convertView) {
        super(convertView);
        this.context=context;
        imageView=convertView.findViewById(R.id.search_result_image_view);
        textView2=convertView.findViewById(R.id.result_textView_2);
        textView1=convertView.findViewById(R.id.result_textView_1);
        textView3=convertView.findViewById(R.id.result_textView_3);
        convertView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(menuItem!=null) {
            if (Utils.currentSite.equals(Utils.starmusiq)) {
                Intent intent=new Intent(context,StarmusiqAlbum.class);
                intent.putExtra("link",menuItem.getLink());
                intent.putExtra("iconLink",menuItem.getImageUrl());
                //todo: these should probably be in a string[] array :)
                intent.putExtra("one", menuItem.getOne());
                intent.putExtra("two",menuItem.getTwo());
                intent.putExtra("three",menuItem.getThree());
                context.startActivity(intent);
            } else if(Utils.currentSite.equals(Utils.songslover)){
                String link=menuItem.getLink();
                new Songslover().downloadSongslover(link, this);
            }
        }
    }

    public void bindItem(SearchResultMenuItem item) {
        this.menuItem=item;
        imageView.setImageBitmap(Utils.getBitmapFromURL(item.getImageUrl()));
        textView1.setText(item.getOne());
        textView2.setText(item.getTwo());
        textView3.setText(item.getThree());
    }

    @Override
    public void onURLReady(JSONObject song) {
        try {
            songName=song.getString("name");
            Thread t1=new Utils().downloadFromURL_no_headers(song.getString("url"),
                    this);
            //used while downloading from songslover
            String iconUrl = song.getString("iconUrl");
            Thread t2= new Utils().downloadToByteArray(iconUrl,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invalidURL() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                        .setTitle("Invalid choice")
                        .setMessage("This entry is a video and not an mp3 file. Video downloads are not yet supported." +
                                "Please choose another song.")
                        .setPositiveButton("Okay",null)
                        .show();
            }
        });
    }

    @Override
    public void onSongsloverDownloadComplete(final File file) {
        Log.d("tag","mp3 download complete");
        songFile=file;
        while(icon==null){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        addArtToDownload();
    }

    @Override
    public void onByteArrayDownloadComplete(byte[] arr) {
        Log.d("tag","icon download complete");
        icon=arr;
    }

    private void addArtToDownload(){
        assert (songFile!=null && icon!=null);
        String path=songFile.getAbsolutePath();
        Log.d("tag","inside addArtToDownload");
        Mp3File file= null;
        try {
            file = new Mp3File(songFile);
            Log.d("tag","init file name: "+songFile.getName());
            ID3v2 tag;
            if(file.hasId3v2Tag())
                tag=file.getId3v2Tag();
            else
            {
                tag=new ID3v22Tag();
                file.setId3v2Tag(tag);
            }
            tag.setAlbumImage(icon,"image/jpeg");

            Log.d("tag","new file name: "+new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC),songName).getAbsolutePath());
            songFile=new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC),songName);
            file.save(songFile.getAbsolutePath());
            Log.d("tag","album art saved :)");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        }

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context).setMessage("Downloaded "+songFile.getName())
                        .setPositiveButton("Okay",null).show();
            }
        });
    }
}
