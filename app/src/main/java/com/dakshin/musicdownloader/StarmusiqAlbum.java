package com.dakshin.musicdownloader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StarmusiqAlbum extends AppCompatActivity {
    private String albumName,two,three,iconLink,link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starmusiq_album);
        Intent intent=getIntent();
        albumName=intent.getStringExtra("one");
        two=intent.getStringExtra("two");
        three=intent.getStringExtra("three");
        link=intent.getStringExtra("link");
        iconLink=intent.getStringExtra("iconLink");
        ((ImageView)findViewById(R.id.starmusiq_album_imageview))
                .setImageBitmap(Utils.getBitmapFromURL(iconLink));
        ((TextView)findViewById(R.id.album_textview_1)).setText(albumName);
        ((TextView)findViewById(R.id.album_textview_2)).setText(two);
        ((TextView)findViewById(R.id.album_textview_3)).setText(three);
    }
    public void download160(View v) {
        //todo
    }
    public void download320(View v) {
        //todo
    }
}
