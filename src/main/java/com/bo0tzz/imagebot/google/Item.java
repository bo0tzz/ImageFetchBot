
package com.bo0tzz.imagebot.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("htmlTitle")
    @Expose
    private String htmlTitle;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("displayLink")
    @Expose
    private String displayLink;
    @SerializedName("snippet")
    @Expose
    private String snippet;
    @SerializedName("htmlSnippet")
    @Expose
    private String htmlSnippet;
    @SerializedName("mime")
    @Expose
    private String mime;
    @SerializedName("image")
    @Expose
    private Image image;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public void setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDisplayLink() {
        return displayLink;
    }

    public void setDisplayLink(String displayLink) {
        this.displayLink = displayLink;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getHtmlSnippet() {
        return htmlSnippet;
    }

    public void setHtmlSnippet(String htmlSnippet) {
        this.htmlSnippet = htmlSnippet;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
