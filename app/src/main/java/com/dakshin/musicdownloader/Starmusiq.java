package com.dakshin.musicdownloader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class Starmusiq {
    JSONObject json;
    public String zipUrl(String pageUrl) {
        Document page=Jsoup.parse(pageToString(pageUrl));
        Element div=page.getElementsByClass("inner cover").first();
        String link=div.getElementsByTag("a").first().attr("href");
        return "http://www.starfile.fun/download-7s-zip-new/"+link;
    }
    public String songUrl(String pageUrl){
        Document page=Jsoup.parse(pageToString(pageUrl));
        Element div=page.getElementsByClass("inner cover").first();
        String link=div.getElementsByTag("a").first().attr("href");
        return "http://www.starfile.fun/download-7s-sng-new/"+link;
    }
    public JSONObject searchStarmusiq(final String searchterm) {
        final JSONObject resultsJSON=new JSONObject();
        final Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URL url = new URL("https://www.starmusiq.fun/search/search-for-"+searchterm+"-movies-starmusiq.html");
                    Document doc = Jsoup.parse(url, 30000);
                    //todo: what if the search result has more than one page?
                    Elements results= doc.getElementsByClass("img-thumbnail");

                    JSONArray resultsArray=new JSONArray();
                    for(Element result:results) {
                        Element pic=result.getElementsByTag("img").first();
                        if(pic==null)
                            continue;
                        String picUrl="https://starmusiq.fun"+pic.attr("src");
                        String albumName=result.getElementsByClass("text-info").first().text();
                        String composerName=result.getElementsByClass("text-warning").first().text();
                        String actors=result.getElementsByTag("span").first().attr("title");
                        String downloadLink="https://starmusiq.fun"+result.getElementsByTag("a").first().attr("href");
                        JSONObject item=new JSONObject();
                        try {
                            item.put("picURL",picUrl);
                            item.put("albumName",albumName);
                            item.put("composerName",composerName);
                            item.put("actors",actors);
                            item.put("link",downloadLink);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        resultsArray.put(item);
                    }
                    try {
                        resultsJSON.put("results",resultsArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        return resultsJSON;
    }

    public JSONObject openAlbum(String albumURL) {
        Document doc= Jsoup.parse(pageToString(albumURL));
        Element table=doc.getElementsByClass("table table-condensed table-hover small").first();
        JSONObject albumJSON=new JSONObject();
        JSONArray songsArray=new JSONArray();
        String kbps_160_AlbumLink,kbps_320_AlbumLink;
        Elements rows=table.getElementsByTag("tr");
        for(int i=0;i<rows.size();i++)
        {
            Element row1=rows.get(i++);
            Element row2=rows.get(i++);
            Element row3=rows.get(i);
            String songName=row1.getElementsByTag("strong").first().text();

            Elements songLinks= row2.getElementsByTag("a");
            String songLink160kbps,songLink320kbps;
            songLink160kbps=songLinks.first().attr("href");
            songLink320kbps=songLinks.last().attr("href");
            String singers="";
            for(Element singer:row3.getElementsByTag("a"))
                singers+=singer.text()+", ";
            singers=singers.substring(0,singers.length()-1);

            JSONObject item=new JSONObject();
            try {
                item.put("name",songName);
                item.put("160link",songLink160kbps);
                item.put("320link",songLink320kbps);
                item.put("singers",singers);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            songsArray.put(item);
        }
        Element albumLinksDiv=doc.getElementsByClass("col-md-8").last();
        Elements albumLinks=albumLinksDiv.getElementsByTag("a");
        kbps_160_AlbumLink=albumLinks.get(0).attr("href");
        kbps_320_AlbumLink=albumLinks.get(1).attr("href");
        try {
            albumJSON.put("albumContents",songsArray);
            albumJSON.put("160kbpsZip",kbps_160_AlbumLink);
            albumJSON.put("320kbpsZip",kbps_320_AlbumLink);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return albumJSON;
    }
    private String pageToString(String link) {
        TrustManager[] dummyTrustManager = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, dummyTrustManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader br= null;
        try {
            br = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s;
        StringBuilder p = null;
        try {
            while((s=br.readLine())!=null)
                p = (p == null ? new StringBuilder() : p).append(s);
        } catch (IOException e) {
            Log.e("tag","ioexception in pagetostring");
        }

        return p != null ? p.toString() : null;
    }
    private void writeToFile(String s){
        PrintWriter writer= null;
        try {
            writer = new PrintWriter("file.txt");
            writer.println(s);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}