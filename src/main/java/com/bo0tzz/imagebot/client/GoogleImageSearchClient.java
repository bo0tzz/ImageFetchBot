package com.bo0tzz.imagebot.client;

import com.bo0tzz.imagebot.google.GoogleSearchResponse;
import com.bo0tzz.imagebot.google.NextPage;
import com.google.common.collect.Iterators;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

public class GoogleImageSearchClient {

    private final Iterator<String> keys;

    public static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1?key=%s&cx=016322137100648159445:e9nsxf_q_-m&searchType=image&q=";
    public static final String PAGING_STRING = "&start=%d";

    public GoogleImageSearchClient(List<String> keys) {

        this.keys = Iterators.cycle(keys);

    }

    public GoogleSearchResponse getImageResults(String query) {

        HttpResponse<GoogleSearchResponse> googleSearchResponseHttpResponse;

        try {

            String url = this.getUrl(query, 0);
            googleSearchResponseHttpResponse = Unirest.get(url).asObject(GoogleSearchResponse.class);

        } catch (UnsupportedEncodingException | UnirestException e) {

            //TODO add logging
            return null;

        }

        return googleSearchResponseHttpResponse.getBody();

    }

    public GoogleSearchResponse getNextPage(GoogleSearchResponse previous) {

        NextPage nextPage = previous.getQueries().getNextPage().get(0);

        Integer startIndex = nextPage.getStartIndex();
        String query = nextPage.getSearchTerms();


        HttpResponse<GoogleSearchResponse> googleSearchResponseHttpResponse;

        try {

            String url = this.getUrl(query, startIndex);
            googleSearchResponseHttpResponse = Unirest.get(url).asObject(GoogleSearchResponse.class);

        } catch (UnsupportedEncodingException | UnirestException e) {

            //TODO add logging
            return null;

        }

        return googleSearchResponseHttpResponse.getBody();

    }

    private String getUrl(String query, int startIndex) throws UnsupportedEncodingException {

        String formattedUrl = String.format(GOOGLE_URL, this.getGoogleAPIKey());

        String formattedPagingString = String.format(PAGING_STRING, startIndex);

        String encodedQuery = URLEncoder.encode(query, "UTF-8");

        return formattedUrl + encodedQuery + formattedPagingString;

    }

    private String getGoogleAPIKey() {

        return keys.next();

    }
}
