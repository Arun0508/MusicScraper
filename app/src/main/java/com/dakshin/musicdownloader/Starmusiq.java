package com.dakshin.musicdownloader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Starmusiq {
    JSONObject json;
    public String zipUrl(final String pageUrl) {
        final StringBuilder link=new StringBuilder();
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                Document page=Jsoup.parse(pageToString(pageUrl));
                Element div=page.getElementsByClass("inner cover").first();
                link.append(div.getElementsByTag("a").first().attr("href"));
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "http://www.starfile.fun/download-7s-zip-new/"+link.toString();
    }
    public String songUrl(final String pageUrl){
//        Document page=Jsoup.parse(pageToString(pageUrl));
        Document page= null;
        final StringBuilder link=new StringBuilder();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document page = Jsoup.parse(new URL(pageUrl),30000);
                    Element div=page.getElementsByClass("inner cover").first();
                    link.append(div.getElementsByTag("a").first().attr("href"));
                } catch (IOException e) {
                    Log.e("tag","xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                    Log.e("tag",e.getLocalizedMessage());
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
        return "http://www.starfile.fun/download-7s-sng-new/"+link.toString();
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

    public JSONObject openAlbum(final String albumURL) {
        final JSONObject albumJSON=new JSONObject();
        final JSONArray songsArray=new JSONArray();
        Log.d("tag", "open album url " + albumURL);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = Jsoup.parse(pageToString(albumURL));
                Element table = doc.getElementsByClass("table table-condensed table-hover small").first();
                String kbps_160_AlbumLink, kbps_320_AlbumLink;
                Element albumLinksDiv = doc.getElementsByClass("col-md-8").last();
                Elements albumLinks = albumLinksDiv.getElementsByTag("a");
                kbps_160_AlbumLink = albumLinks.get(0).attr("href");
                kbps_320_AlbumLink = albumLinks.get(1).attr("href");
                try {
                    albumJSON.put("albumContents", otherAlbumOpenMethod(table));
                    albumJSON.put("160kbpsZip", kbps_160_AlbumLink);
                    albumJSON.put("320kbpsZip", kbps_320_AlbumLink);
                } catch (JSONException e) {
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

        return albumJSON;
    }
    private JSONArray otherAlbumOpenMethod(Element table) {
        JSONArray array=new JSONArray();
        Elements rows=table.getElementsByTag("tr");
        for(int i=0;i<rows.size();i++) {
            Element row1=rows.get(i++);
            Element row2=rows.get(i);
            JSONObject item=new JSONObject();
            String songName=row1.getElementsByTag("strong").first().text();
            Elements singeratags=row2.getElementsByTag("a");
            String singers="";
            for(Element j:singeratags) {
                singers+=j.text()+", ";
            }
            singers=singers.substring(0,singers.length()-2);
            String link160,link320;
            Elements links=row1.getElementsByClass("text-right").first().getElementsByTag("a");
            link160=links.get(0).attr("href");
            link320=links.get(0).attr("href");
            try {
                item.put("name",songName);
                item.put("160link",link160);
                item.put("320link",link320);
                item.put("singers",singers);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(item);
        }
        return array;
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
        BufferedReader br;
        StringBuilder p=null;
        try {
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String s;
            try {
                while((s=br.readLine())!=null)
                    p = (p == null ? new StringBuilder() : p).append(s);
            } catch (IOException e) {
                Log.e("tag","ioexception in pagetostring");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p != null ? p.toString() : null;
    }
}
