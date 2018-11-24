package com.dakshin.musicdownloader;


public class SearchResultMenuItem {
    private String imageUrl,one,two,three,link;

    public SearchResultMenuItem(String imageUrl, String one, String two, String three,String link) {
        this.imageUrl = imageUrl;
        this.one = one;
        this.two = two;
        this.three = three;
        this.link=link;
    }

    public SearchResultMenuItem() {
    }
    public void setLink(String link) {
        this.link=link;
    }
    public String getLink() {
        return link;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public String getThree() {
        return three;
    }

    public void setThree(String three) {
        this.three = three;
    }
}
