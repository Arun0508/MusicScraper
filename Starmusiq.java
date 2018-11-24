import org.json.JSONArray;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;

public class Starmusiq {
    public static String zipUrl(String pageUrl) throws Exception{
        Document page=Jsoup.parse(pageToString(pageUrl));
        Element div=page.getElementsByClass("inner cover").first();
        String link=div.getElementsByTag("a").first().attr("href");
        return "http://www.starfile.fun/download-7s-zip-new/"+link;
    }
    public static String songUrl(String pageUrl) throws Exception{
        Document page=Jsoup.parse(pageToString(pageUrl));
        Element div=page.getElementsByClass("inner cover").first();
        String link=div.getElementsByTag("a").first().attr("href");
        return "http://www.starfile.fun/download-7s-sng-new/"+link;
    }
    public static JSONObject searchStarmusiq(String searchterm) {
        Document doc = null;
        try {
            URL url = new URL("https://www.starmusiq.fun/search/search-for-"+searchterm+"-movies-starmusiq.html");
            doc = Jsoup.parse(url, 30000);
        } catch (MalformedURLException e) {
            System.out.println("Invalid search term?");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
        if(doc==null)
            return null;
        //todo: what if the search result has more than one page?
        Elements results=doc.getElementsByClass("img-thumbnail");
        JSONObject resultsJSON=new JSONObject();
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
            item.put("picURL",picUrl);
            item.put("albumName",albumName);
            item.put("composerName",composerName);
            item.put("actors",actors);
            item.put("link",downloadLink);
            resultsArray.put(item);
        }
        resultsJSON.put("results",resultsArray);
        return resultsJSON;
    }

    public static JSONObject openAlbum(String albumURL) throws Exception{
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
            item.put("name",songName);
            item.put("160link",songLink160kbps);
            item.put("320link",songLink320kbps);
            item.put("singers",singers);
            songsArray.put(item);
        }
        Element albumLinksDiv=doc.getElementsByClass("col-md-8").last();
        Elements albumLinks=albumLinksDiv.getElementsByTag("a");
        kbps_160_AlbumLink=albumLinks.get(0).attr("href");
        kbps_320_AlbumLink=albumLinks.get(1).attr("href");
        albumJSON.put("albumContents",songsArray);
        albumJSON.put("160kbpsZip",kbps_160_AlbumLink);
        albumJSON.put("320kbpsZip",kbps_320_AlbumLink);
        return albumJSON;
    }
    private static String pageToString(String link) throws Exception{
        TrustManager[] dummyTrustManager = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, dummyTrustManager, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        URL url = new URL(link);
//        URL url=new URL("https://google.com");
        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
        String s;
        StringBuilder p = null;
        while((s=br.readLine())!=null)
            p = (p == null ? new StringBuilder() : p).append(s);

        return p != null ? p.toString() : null;
    }
    private static void writeToFile(String s)throws Exception{
        PrintWriter writer=new PrintWriter("file.txt");
        writer.println(s);
        writer.close();
    }
}
