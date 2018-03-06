
package com.bo0tzz.imagebot.google;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.bo0tzz.imagebot.google.error.GoogleError;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleSearchResponse {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("url")
    @Expose
    private Url url;
    @SerializedName("queries")
    @Expose
    private Queries queries;
    @SerializedName("context")
    @Expose
    private Context context;
    @SerializedName("searchInformation")
    @Expose
    private SearchInformation searchInformation;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    @SerializedName("error")
    @Expose
    private GoogleError error;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public Queries getQueries() {
        return queries;
    }

    public void setQueries(Queries queries) {
        this.queries = queries;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SearchInformation getSearchInformation() {
        return searchInformation;
    }

    public void setSearchInformation(SearchInformation searchInformation) {
        this.searchInformation = searchInformation;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Item getRandomItem() {

        return items.get(ThreadLocalRandom.current().nextInt(items.size()));

    }

    public GoogleError getError() {
        return error;
    }

    public void setError(GoogleError error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

}
