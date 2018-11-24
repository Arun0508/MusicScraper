package com.dakshin.musicdownloader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        ListView listView=findViewById(R.id.starmusiq_album_listview);
        final ArrayList<String> arrayList=new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, arrayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(arrayList.get(2*position));
                text2.setText(arrayList.get(2*position+1));
                return view;
            }
            @Override
            public int getCount()
            {
                return arrayList.size()/2;
            }
        };
        listView.setAdapter(adapter);
        Starmusiq starmusiq=new Starmusiq();
        try {
            JSONArray albumJson = starmusiq.openAlbum(link).getJSONArray("albumContents");
            for(int i=0;i<albumJson.length();i++)
            {
                JSONObject item=albumJson.getJSONObject(i);
                arrayList.add(item.getString("name"));
                arrayList.add(item.getString("singers"));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void download160(View v) {
        //todo
    }
    public void download320(View v) {
        //todo
    }
}
