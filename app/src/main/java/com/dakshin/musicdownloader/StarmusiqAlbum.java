package com.dakshin.musicdownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        final Starmusiq starmusiq=new Starmusiq();
        JSONArray albumJson=null;
        try {
            albumJson = starmusiq.openAlbum(link).getJSONArray("albumContents");
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
        final JSONArray finalAlbumJson = albumJson;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final JSONObject item= finalAlbumJson.getJSONObject(position);
                    AlertDialog.Builder builder=new AlertDialog.Builder(StarmusiqAlbum.this);
                    builder.setTitle("Choose download quality");
                    builder.setPositiveButton("160 kbps", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DownloadManager manager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                            try {
                                String link=item.getString("160link");
                                link=starmusiq.songUrl(link);
                                Log.d("tag","160link: "+link);
                                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(link));
                                request.setTitle("download");
                                request.setDescription("music");
                                request.setDestinationInExternalFilesDir(StarmusiqAlbum.this,Environment.DIRECTORY_MUSIC,"download.mp3");
                                request.allowScanningByMediaScanner();
                                manager.enqueue(request);
                                Log.d("tag","request enqueed.");
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                startActivity(browserIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton("320 kbps", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DownloadManager manger=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                            try {
                                DownloadManager.Request request=new DownloadManager.Request(
                                        Uri.parse(item.getString("320link"))
                                );
                                request.allowScanningByMediaScanner();
                                manger.enqueue(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void download160(View v) {
        //todo
    }
    public void download320(View v) {
        //todo
    }
}
