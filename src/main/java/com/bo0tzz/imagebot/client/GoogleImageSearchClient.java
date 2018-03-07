package com.bo0tzz.imagebot.client;

import com.bo0tzz.imagebot.ImageFetcherBot;
import com.bo0tzz.imagebot.google.GoogleSearchResponse;
import com.bo0tzz.imagebot.google.NextPage;
import com.bo0tzz.imagebot.google.error.GoogleError;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

public class GoogleImageSearchClient {

    private final Iterator<String> keys;
    private final Gson gson = new Gson();

    public static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1?key=%s&cx=016322137100648159445:e9nsxf_q_-m&searchType=image&q=";
    public static final String PAGING_STRING = "&start=%d";

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleImageSearchClient.class);

    public GoogleImageSearchClient(List<String> keys) {

        if (keys.isEmpty()) {

            LOGGER.error("You didn't give me any keys! Please supply google API keys.");
            System.exit(1);

        }

        this.keys = Iterators.cycle(keys);

    }

    public GoogleSearchResponse getImageResults(String query) {

        LOGGER.debug("Searching images for query: {}.", query);

        HttpResponse<JsonNode> jsonNodeHttpResponse;

        try {

            String url = this.getUrl(query, 0);
            jsonNodeHttpResponse = Unirest.get(url).asJson();
            //TODO migrate to com.squareup.okhttp3

        } catch (UnsupportedEncodingException | UnirestException ex) {

            ImageFetcherBot.handleError(ex);

            GoogleError error = new GoogleError();
            error.setMessage(ex.getMessage());

            GoogleSearchResponse errorResponse = new GoogleSearchResponse();
            errorResponse.setError(error);

            return errorResponse;

        }

        return gson.fromJson(jsonNodeHttpResponse.getBody().toString(), GoogleSearchResponse.class);

    }

    public GoogleSearchResponse getNextPage(GoogleSearchResponse previous) {

        NextPage nextPage = previous.getQueries().getNextPage().get(0);

        Integer startIndex = nextPage.getStartIndex();
        String query = nextPage.getSearchTerms();


        HttpResponse<GoogleSearchResponse> googleSearchResponseHttpResponse;

        try {

            String url = this.getUrl(query, startIndex);
            googleSearchResponseHttpResponse = Unirest.get(url).asObject(GoogleSearchResponse.class);

        } catch (UnsupportedEncodingException | UnirestException ex) {

            ImageFetcherBot.handleError(ex);
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
