package com.bo0tzz.imagebot.handler;

import com.bo0tzz.imagebot.ImageFetcherBot;
import com.bo0tzz.imagebot.client.GoogleImageSearchClient;
import com.bo0tzz.imagebot.google.GoogleSearchResponse;
import com.bo0tzz.imagebot.google.Item;
import com.jtelegram.api.commands.Command;
import com.jtelegram.api.commands.CommandHandler;
import com.jtelegram.api.events.message.TextMessageEvent;
import com.jtelegram.api.ex.TelegramException;
import com.jtelegram.api.message.impl.PhotoMessage;
import com.jtelegram.api.message.impl.TextMessage;
import com.jtelegram.api.message.input.file.ExternalInputFile;
import com.jtelegram.api.requests.message.send.SendPhoto;

import java.net.MalformedURLException;
import java.net.URL;

public class ImageCommandHandler implements CommandHandler {

    private final GoogleImageSearchClient googleImageSearchClient;
    private final ImageFetcherBot imageFetcherBot;

    public ImageCommandHandler(GoogleImageSearchClient googleImageSearchClient, ImageFetcherBot imageFetcherBot) {

        this.googleImageSearchClient = googleImageSearchClient;
        this.imageFetcherBot = imageFetcherBot;

    }

    @Override
    public void onCommand(TextMessageEvent textMessageEvent, Command command) {

        TextMessage baseMessage = command.getBaseMessage();

        String query = command.getArgsAsText();

        GoogleSearchResponse imageResults = googleImageSearchClient.getImageResults(query);

        if (imageResults == null) {

            //TODO return error to user
            return;

        }

        Item item = imageResults.getRandomItem();

        URL photoUrl;

        try {

            photoUrl = new URL(item.getLink());

        } catch (MalformedURLException e) {

            e.printStackTrace();
            //TODO return error to user
            return;

        }

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(command.getChat().getChatId())
                .photo(new ExternalInputFile(photoUrl))
                .callback(this::consumePhotoMessage)
                .errorHandler(e -> this.errorHandler(e, baseMessage))
                .build();

        //TODO send photo

    }

    public void errorHandler(TelegramException ex, TextMessage baseMessage) {

        //TODO handle error - how do I send this to the user?

    }

    public void consumePhotoMessage(PhotoMessage photoMessage) {

        //TODO log successfully sent messages?

    }

}
