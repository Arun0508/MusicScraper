package com.dakshin.musicdownloader;


import android.graphics.Bitmap;

public class SearchResultMenuItem {
    private String imageUrl,one,two,three,link;
    private Bitmap icon;
    public SearchResultMenuItem(String imageUrl, String one, String two, String three,String link) {
        this.imageUrl = imageUrl;
        this.one = one;
        this.two = two;
        this.three = three;
        this.link=link;
        icon=Utils.getBitmapFromURL(imageUrl);
    }

    SearchResultMenuItem() {
    }
    public void setLink(String link) {
        this.link=link;
    }
    public String getLink() {
        return link;
    }
    String getImageUrl() {
        return imageUrl;
    }

    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        icon=Utils.getBitmapFromURL(imageUrl);
    }
    public Bitmap getIcon()
    {
        return icon;
    }
    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    String getTwo() {
        return two;
    }

    void setTwo(String two) {
        this.two = two;
    }

    String getThree() {
        return three;
    }

    void setThree(String three) {
        this.three = three;
    }
}
