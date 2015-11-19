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
    private final String url;

    public ImageCommandListener(ImageBot bot) {
        this.bot = bot;
        url = "https://www.googleapis.com/customsearch/v1?key=" + bot.getKey() + "&cx=016322137100648159445:e9nsxf_q_-m&searchType=image&q=";
    }

    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        if (event.getCommand().equals("get")) {

            event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOADING_PHOTO).build(), ImageBot.bot);

            HttpResponse<JsonNode> response = null;
            try {
                response = Unirest.get(url + event.getArgsString().replace(" ", "+"))
                        .asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            JSONArray array = null;
            try {
                array = response.getBody().getObject().getJSONArray("items");
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("on: " + response.getBody());
                event.getChat().sendMessage("Something went wrong while getting the image!", ImageBot.bot);
            }
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
        }
    }
}
