package com.dakshin.musicdownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

 class Songslover {
    public static void searchSongslover(NetworkCallCompleteListener callback,String search) {
        class X extends Thread {
            String search;
            NetworkCallCompleteListener callback;
            X(String a, NetworkCallCompleteListener callback) {
                search=a;
                this.callback=callback;
                this.start();
            }
            public void run() {
                String input=search.replace(' ','+');
                Document doc= null;
                try {
                    doc = Jsoup.parse(new URL("http://songslover.live/?s="+input),50000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements articles=doc.getElementsByTag("article");
                JSONArray results=new JSONArray();
                JSONObject result=null;
                for(Element article:articles) {
                    Element link = article.getElementsByTag("h2").first().getElementsByTag("a").first();
                    String trackName = link.text();
                    String trackurl = link.attr("href");
                    String iconUrl;
                    try {
                        iconUrl = article.getElementsByTag("img").first().attr("src");
                    } catch (NullPointerException e) {
                        continue;
                    }
                    JSONObject item = new JSONObject();
                    try {
                        item.put("name", trackName);
                        item.put("url", trackurl);
                        item.put("icon", iconUrl);

                        results.put(item);

                        result = new JSONObject();
                        result.put("results", results);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.networkCallComplete("songslover",result);
            }
        }
        new X(search,callback);
    }
    public static JSONObject downloadSongslover(String url) throws NotASongLinkException{
        Document page= null;
        try {
            page = Jsoup.parse(new URL(url),50000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element entry=page.getElementsByClass("entry").first();
        Elements p=entry.getElementsByTag("p");
        Element span=p.get(1);
        String link;
        try {
            link = span.getElementsByTag("a").first().attr("href");
        } catch (NullPointerException e) {
            throw new NotASongLinkException();
        }
        String[] x=link.split("/");
        String name=x[x.length-1].replace("%20"," ");
        JSONObject song=new JSONObject();
        try {
            song.put("name",name);
            song.put("url",link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return song;
    }

}

class NotASongLinkException extends Exception {}