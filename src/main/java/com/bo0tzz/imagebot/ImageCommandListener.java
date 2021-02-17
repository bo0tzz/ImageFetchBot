package com.bo0tzz.imagebot;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.inline.send.content.InputTextMessageContent;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultArticle;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultPhoto;
import pro.zackpollard.telegrambot.api.chat.message.send.*;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by bo0tzz
 */
public class ImageCommandListener implements Listener {
    private ImageBot bot;
    private final String[] keys;
    private int lastKey = 0;
    private final String giphyAPI;

    public ImageCommandListener(ImageBot bot) {
        this.bot = bot;
        this.keys = bot.getKeys();
        this.giphyAPI = "http://api.giphy.com/v1/gifs/translate?api_key=" + bot.getGiphyKey() + "&s=";
    }

    @Override
    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        String query = event.getQuery().getQuery();
        if (query == null || query.equals("")) {
            return;
        }
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(getUrl() + URLEncoder.encode(query, "UTF-8"))
                    .asJson();
        } catch (UnirestException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONArray array = null;
        if (response.getBody().getObject().has("items")) {
            array = response.getBody().getObject().getJSONArray("items");
        } else if (response.getBody().getObject().has("error")) {
            InlineQueryResultArticle result = InlineQueryResultArticle.builder()
                    .title("The bot has reached its request limit for today!")
                    .description("Unfortunately, Google limits the amount of requests this bot can make every day.")
                    .inputMessageContent(InputTextMessageContent.builder()
                        .messageText("Unfortunately this bot has reached the daily limit on how many images it can fetch for you. " +
                                "Please be patient, the limit should reset within 24 hours.\n")
                        .parseMode(ParseMode.MARKDOWN)
                        .build())
                    .build();
            event.getQuery().answer(ImageBot.bot, InlineQueryResponse.builder().results(result).build());
            return;
        }
        if (array == null) return;
        List<InlineQueryResult> responses = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject image = array.getJSONObject(i);
            URL url = null;
            URL thumb = null;
            try {
                url = new URL(image.getString("link"));
                thumb = new URL(image.getJSONObject("image").getString("thumbnailLink"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //getting width and height of image, so Telegram will show the Response properly
            int width = 0;
            int height = 0;
            
            width = image.getJSONObject("image").getInt("width");
            height = image.getJSONObject("image").getInt("height");
            
            InlineQueryResultPhoto result = InlineQueryResultPhoto.builder().photoUrl(url).photoWidth(width).photoHeight(height).thumbUrl(thumb).build();
            responses.add(result);
        }
        event.getQuery().answer(ImageBot.bot, InlineQueryResponse.builder().results(responses).build());

    }

    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        if (event.getCommand().equals("get")) {
            if (event.getArgsString().equals("")) {
                event.getChat().sendMessage("Give me");
                return;
            }

            event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOADING_PHOTO).build());

            HttpResponse<JsonNode> response = null;
            try {
                response = Unirest.get(getUrl() + URLEncoder.encode(event.getArgsString(), "UTF-8"))
                        .asJson();
            } catch (UnirestException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (response.getBody().getObject().has("error")) {
                event.getChat().sendMessage("The Google API returned the following error - " + response.getBody().getObject().getJSONObject("error").getString("message"));
                System.out.println("Google API returned error: " + response.getBody());
                return;
            }

            JSONArray array;
            if (response.getBody().getObject().has("items")) {
                array = response.getBody().getObject().getJSONArray("items");
            } else {
                event.getChat().sendMessage("No images found!");
                return;
            }
            if (array.length() == 0) {
                event.getChat().sendMessage("No images found!");
                return;
            }
            JSONObject image = array.getJSONObject(ThreadLocalRandom.current().nextInt(array.length()));
            URL url;
            try {
                url = new URL(image.getString("link"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                event.getChat().sendMessage("Something went wrong while getting the image!");
                return;
            }
            System.out.println("Uploading photo: " + url);
            event.getChat().sendMessage(SendablePhotoMessage.builder()
                    .photo(new InputFile(url))
                    .replyTo(event.getMessage())
                    .build());
            System.out.println("Photo uploaded: " + url);
        } else if (event.getCommand().equals("getgif")) {

            event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOAD_DOCUMENT).build());

            URI request;
            try {
                request = new URI(giphyAPI + URLEncoder.encode(event.getArgsString(), "UTF-8"));
            } catch (URISyntaxException e) {
                event.getChat().sendMessage("Request contained illegal characters!");
                e.printStackTrace();
                return;
            } catch (UnsupportedEncodingException e) {
                event.getChat().sendMessage("URL encoding failed!");
                e.printStackTrace();
                return;
            }

            HttpResponse<JsonNode> response = null;
            try {
                response = Unirest.get(request.toString())
                        .asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            URL url = null;
            try {
                JSONObject image = response.getBody().getObject().getJSONObject("data").getJSONObject("images").getJSONObject("original");
                url = new URL(image.getString("url"));
            } catch (MalformedURLException|JSONException e) {
                System.out.println("Error on response: " + response.getBody());
                e.printStackTrace();
            }
            if (url == null) {
                event.getChat().sendMessage("No pictures found!");
                return;
            }
            System.out.println("Uploading gif: " + url);
            event.getChat().sendMessage(SendableDocumentMessage.builder()
                .document(new InputFile(url))
                .replyTo(event.getMessage())
                .build());
        }
    }

    private String getUrl() {

        int chosenKey = ++lastKey;

        if(chosenKey >= keys.length) {

            chosenKey = 0;
            lastKey = 0;
        }

        return "https://www.googleapis.com/customsearch/v1?key=" + keys[chosenKey] + "&cx=016322137100648159445:e9nsxf_q_-m&searchType=image&q=";
    }
}
