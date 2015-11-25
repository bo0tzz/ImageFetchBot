package com.bo0tzz.imagebot;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.message.send.ChatAction;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableChatAction;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;
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

    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        if (event.getCommand().equals("get")) {

            event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOADING_PHOTO).build(), ImageBot.bot);

            HttpResponse<JsonNode> response = null;
            try {
                response = Unirest.get(getUrl() + event.getArgsString().replace(" ", "+"))
                        .asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            if (response.getBody().getObject().has("error")) {
                event.getChat().sendMessage("The Google API returned the following error - " + response.getBody().getObject().getJSONObject("error").getString("message"), ImageBot.bot);
                System.out.println("Google API returned error: " + response.getBody());
                return;
            }

            JSONArray array = response.getBody().getObject().getJSONArray("items");
            if (array.length() == 0) {
                event.getChat().sendMessage("No images found!", ImageBot.bot);
                return;
            }
            JSONObject image = array.getJSONObject(ThreadLocalRandom.current().nextInt(array.length()));
            URL url;
            try {
                url = new URL(image.getString("link"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                event.getChat().sendMessage("Something went wrong while getting the image!", ImageBot.bot);
                return;
            }
            System.out.println("Uploading photo: " + url);
            event.getChat().sendMessage(SendablePhotoMessage.builder()
                    .photo(new InputFile(url))
                    .replyTo(event.getMessage())
                    .build(), ImageBot.bot);
            System.out.println("Photo uploaded: " + url);
        } else if (event.getCommand().equals("getgif")) {

            event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOADING_PHOTO).build(), ImageBot.bot);

            HttpResponse<JsonNode> response = null;
            try {
                response = Unirest.get(giphyAPI + event.getArgsString().replace(" ", "+"))
                        .asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            URL url = null;
            try {
                JSONObject image = response.getBody().getObject().getJSONObject("data").getJSONObject("images").getJSONObject("original");
                url = new URL(image.getString("url"));
            } catch (MalformedURLException|JSONException e) {
                e.printStackTrace();
            }
            System.out.println("Uploading gif: " + url);
            event.getChat().sendMessage(SendablePhotoMessage.builder()
                .photo(new InputFile(url))
                .replyTo(event.getMessage())
                .build(), ImageBot.bot);
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
