
package com.bo0tzz.imagebot.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchInformation {

    @SerializedName("searchTime")
    @Expose
    private Double searchTime;
    @SerializedName("formattedSearchTime")
    @Expose
    private String formattedSearchTime;
    @SerializedName("totalResults")
    @Expose
    private String totalResults;
    @SerializedName("formattedTotalResults")
    @Expose
    private String formattedTotalResults;

    public Double getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Double searchTime) {
        this.searchTime = searchTime;
    }

    public String getFormattedSearchTime() {
        return formattedSearchTime;
    }

    public void setFormattedSearchTime(String formattedSearchTime) {
        this.formattedSearchTime = formattedSearchTime;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getFormattedTotalResults() {
        return formattedTotalResults;
    }

    public void setFormattedTotalResults(String formattedTotalResults) {
        this.formattedTotalResults = formattedTotalResults;
    }

}
