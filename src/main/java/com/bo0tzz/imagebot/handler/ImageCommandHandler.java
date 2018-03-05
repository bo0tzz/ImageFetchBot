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
import com.jtelegram.api.requests.message.send.SendText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class ImageCommandHandler implements CommandHandler {

    private final GoogleImageSearchClient googleImageSearchClient;
    private final ImageFetcherBot imageFetcherBot;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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
                .replyToMessageId(baseMessage.getMessageId())
                .photo(new ExternalInputFile(photoUrl))
                .callback(this::consumePhotoMessage)
                .errorHandler(e -> this.errorHandler(e, baseMessage))
                .build();

        imageFetcherBot.getBot().perform(sendPhoto);

    }

    public void errorHandler(TelegramException ex, TextMessage baseMessage) {

        ImageFetcherBot.handleError(ex);

        String errorMessage = "I encountered an error while trying to find your image!\n" +
                "Please try again. If this keeps happening, please contact my creator @bo0tzz";

        SendText sendText = SendText.builder()
                .text(errorMessage)
                .chatId(baseMessage.getChat().getChatId())
                .replyToMessageID(baseMessage.getMessageId())
                .errorHandler(ImageFetcherBot::handleFatalError)
                .build();

        imageFetcherBot.getBot().perform(sendText);

    }

    public void consumePhotoMessage(PhotoMessage photoMessage) {

        LOGGER.trace("Sent photo {} to chat {}", photoMessage.getMessageId(), photoMessage.getChat().getChatId().getId());

    }

}
