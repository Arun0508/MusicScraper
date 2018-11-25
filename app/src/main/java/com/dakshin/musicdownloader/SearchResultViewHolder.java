package com.dakshin.musicdownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLConnection;

public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
SongsLoverListener,DownloadCompleteListener{
    ImageView imageView;
    TextView textView1, textView2,textView3;
    SearchResultMenuItem menuItem;
    private Context context;

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
            new Utils().downloadFromURL_no_headers(song.getString("url"),
                    song.getString("name"),
                    this);
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
    public void onDownloadComplete(final File file) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Downloaded "+file.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
