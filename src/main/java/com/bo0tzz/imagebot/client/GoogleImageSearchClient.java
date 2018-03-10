package com.bo0tzz.imagebot.client;

import com.bo0tzz.imagebot.ImageFetcherBot;
import com.bo0tzz.imagebot.google.GoogleSearchResponse;
import com.bo0tzz.imagebot.google.NextPage;
import com.bo0tzz.imagebot.google.error.GoogleError;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

public class GoogleImageSearchClient {

    private final Iterator<String> keys;
    private final Gson gson = new Gson();
    private final OkHttpClient httpClient = new OkHttpClient();

    public static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1?key=%s&cx=016322137100648159445:e9nsxf_q_-m&searchType=image&q=";
    public static final String PAGING_STRING = "&start=%d";

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleImageSearchClient.class);
    private static final String LOG_KEY_FILTER = "key=(.*)&search";
    private static final String REDACTED_KEY = "key=<redacted>&search";

    public GoogleImageSearchClient(List<String> keys) {

        if (keys.isEmpty()) {

            LOGGER.error("You didn't give me any keys! Please supply google API keys.");
            System.exit(1);

        }

        this.keys = Iterators.cycle(keys);

    }

    public GoogleSearchResponse getImageResults(String query) {

        LOGGER.debug("Searching images for query: {}.", query);

        try {

            String url = this.getUrl(query, 1);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            LOGGER.debug("Sending request to google: {}", request.toString().replaceAll(LOG_KEY_FILTER, REDACTED_KEY));
            Response response = httpClient.newCall(request).execute();
            LOGGER.debug("Received response from google: {}", request.toString().replaceAll(LOG_KEY_FILTER, REDACTED_KEY));
            return gson.fromJson(response.body().string(), GoogleSearchResponse.class);

        } catch (IOException ex) {

            ImageFetcherBot.handleError(ex);

            GoogleError error = new GoogleError();
            error.setMessage(ex.getMessage());

            GoogleSearchResponse errorResponse = new GoogleSearchResponse();
            errorResponse.setError(error);

            return errorResponse;

        }

    }

    public GoogleSearchResponse getNextPage(GoogleSearchResponse previous) {

        NextPage nextPage = previous.getQueries().getNextPage().get(0);

        Integer startIndex = nextPage.getStartIndex();
        String query = nextPage.getSearchTerms();

        try {

            String url = this.getUrl(query, startIndex);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            LOGGER.debug("Sending request to google: {}", request.toString().replaceAll(LOG_KEY_FILTER, REDACTED_KEY));
            Response response = httpClient.newCall(request).execute();
            LOGGER.debug("Received response from google: {}", request.toString().replaceAll(LOG_KEY_FILTER, REDACTED_KEY));
            return gson.fromJson(response.body().string(), GoogleSearchResponse.class);

        } catch (IOException ex) {

            ImageFetcherBot.handleError(ex);
            return null;

        }

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
