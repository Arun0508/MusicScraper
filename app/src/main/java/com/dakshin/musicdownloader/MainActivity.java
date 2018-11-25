package com.dakshin.musicdownloader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NetworkCallCompleteListener{
    private ArrayList<SearchResultMenuItem> arrayList=new ArrayList<>();
    private RecyclerView listView;
    private SearchResultAdapter adapter;
    private final int STORAGE_REQUEST_CODE=911;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private EditText searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set-up for utils class
        Utils.density=getResources().getDisplayMetrics().density;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this).setTitle("Permission denied")
                        .setMessage("This app cannot run without storage permission.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        STORAGE_REQUEST_CODE);
                            }
                        }).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_REQUEST_CODE);
            }
        }

        searchBar = findViewById(R.id.search_bar);
        listView=findViewById(R.id.results_listview);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        //itemdecorator
        adapter=new SearchResultAdapter(this,arrayList);
        adapter.setHasStableIds(true);
        listView.setAdapter(adapter);
    }
    public void songslover(View v) {
        Utils.currentSite=Utils.songslover;
        arrayList.clear();
        String searchTerm=searchBar.getText().toString();
        Songslover.searchSongslover(this,searchTerm);
        adapter.notifyDataSetChanged();
    }
    public void starmusiq(View v) {
        arrayList.clear();
        Utils.currentSite=Utils.starmusiq;
        String searchTerm=searchBar.getText().toString();
        Starmusiq searcher=new Starmusiq();
        JSONObject searchResults=searcher.searchStarmusiq(searchTerm);
        try {
            JSONArray resultsArray = searchResults.getJSONArray("results");
            for(int i=0;i<resultsArray.length();i++) {
                JSONObject json=resultsArray.getJSONObject(i);
                SearchResultMenuItem item = new SearchResultMenuItem();
                item.setImageUrl(json.getString("picURL"));
                item.setLink(json.getString("link"));
                item.setOne(json.getString("albumName"));
                item.setTwo(json.getString("composerName"));
                item.setThree(json.getString("actors"));
                arrayList.add(item);

            }
        } catch(JSONException e) {
            Log.e("tag","JSONException caught");
        }
        adapter.notifyDataSetChanged();
//        Log.d("tag","size of arraylist: "+arrayList.size());
    }

    public native String stringFromJNI();

    @Override
    public void networkCallComplete(String code,JSONObject object) {
        if(code.equals("songslover")) {
            try {

                JSONArray results=object.getJSONArray("results");
                for(int i=0;i<results.length();i++) {
                    JSONObject item=results.getJSONObject(i);
                    SearchResultMenuItem menuItem=new SearchResultMenuItem();
                    menuItem.setLink(item.getString("url"));
                    menuItem.setOne(item.getString("name"));
                    menuItem.setImageUrl(item.getString("icon"));
                    //dummy
                    menuItem.setTwo("");
                    menuItem.setThree("");
                    arrayList.add(menuItem);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Permission denied")
                                    .setMessage("This app cannot run without storage permission. The app will now exit.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.this.finish();
                                        }
                                    }).show();

                        }
            }
        }
    }
}
