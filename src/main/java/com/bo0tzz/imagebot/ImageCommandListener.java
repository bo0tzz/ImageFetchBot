package com.bo0tzz.imagebot;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.message.send.ChatAction;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableChatAction;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by bo0tzz
 */
public class ImageCommandListener implements Listener {
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        if (event.getCommand().equals("get")) {
            event.getChat().sendMessage(SendableChatAction.builder().chatAction(ChatAction.UPLOADING_PHOTO).build(), ImageBot.bot);
            HttpResponse<JsonNode> response = null;
            try {
                response = Unirest.get("https://api.imgur.com/3/gallery/search/")
                        .header("Authorization", "Client-ID 2b58810c2d5385e")
                        .queryString("sort", "top")
                        .queryString("q_exactly", event.getArgsString())
                        .asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            JSONArray array = response.getBody().getObject().getJSONArray("data");
            JSONObject image = array.getJSONObject(ThreadLocalRandom.current().nextInt(array.length()));
            event.getChat().sendMessage(SendablePhotoMessage.builder()
                    .photo(new InputFile(image.getString("link")))
                    .replyTo(event.getMessage())
                    .build(), ImageBot.bot);
        }
    }
}
