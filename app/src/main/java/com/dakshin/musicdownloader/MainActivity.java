package com.dakshin.musicdownloader;

import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<SearchResultMenuItem> arrayList=new ArrayList<>();
    private RecyclerView listView;
    private SearchResultAdapter adapter;
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




        searchBar = findViewById(R.id.search_bar);
        listView=findViewById(R.id.results_listview);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        //itemdecorator
        adapter=new SearchResultAdapter(this,arrayList);
        listView.setAdapter(adapter);
    }
    long refid;
    public void starmusiq(View v) {
        arrayList.clear();
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
}
