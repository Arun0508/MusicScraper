package com.dakshin.musicdownloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultViewHolder>{
    ArrayList<SearchResultMenuItem> arrayList;
    Context context;

    public SearchResultAdapter(Context context,ArrayList<SearchResultMenuItem> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.result_list_item, viewGroup, false);
        return new SearchResultViewHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder searchResultViewHolder, int i) {
        SearchResultMenuItem item=arrayList.get(i);
        searchResultViewHolder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
