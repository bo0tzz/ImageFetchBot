package com.bo0tzz.imagebot.handler;

import com.bo0tzz.imagebot.ImageFetcherBot;
import com.bo0tzz.imagebot.client.GoogleImageSearchClient;
import com.bo0tzz.imagebot.google.GoogleSearchResponse;
import com.bo0tzz.imagebot.google.Item;
import com.bo0tzz.imagebot.google.error.GoogleError;
import com.bo0tzz.imagebot.utils.Util;
import com.jtelegram.api.commands.Command;
import com.jtelegram.api.commands.CommandHandler;
import com.jtelegram.api.events.message.TextMessageEvent;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCommandHandler.class);

    public static final String ERROR_MESSAGE = "I encountered an error while trying to find your image!\n" +
            "Please try again. If this keeps happening, please contact my creator @bo0tzz";

    public ImageCommandHandler(GoogleImageSearchClient googleImageSearchClient, ImageFetcherBot imageFetcherBot) {

        this.googleImageSearchClient = googleImageSearchClient;
        this.imageFetcherBot = imageFetcherBot;

    }

    @Override
    public void onCommand(TextMessageEvent textMessageEvent, Command command) {

        TextMessage baseMessage = command.getBaseMessage();

        String query = command.getArgsAsText();

        if (Util.isEmpty(query)) {

            String message = "Please send a query to search for! Example:\n/get@ImageFetcherBot dogs";

            SendText sendText = SendText.builder()
                    .text(message)
                    .chatId(baseMessage.getChat().getChatId())
                    .replyToMessageID(baseMessage.getMessageId())
                    .errorHandler(ImageFetcherBot::handleFatalError)
                    .build();

            imageFetcherBot.getBot().perform(sendText);

            return;

        }

        //FUTURE send chat action "uploading"

        LOGGER.debug("Received new image search command: \"{}\" from user {} in chat {}.", query, baseMessage.getFrom().getUsername(), baseMessage.getChat().getChatId().getId());

        GoogleSearchResponse imageResults = googleImageSearchClient.getImageResults(query);

        if (imageResults.hasError()) {

            GoogleError error = imageResults.getError();
            LOGGER.error("Received an error response from google: {}", error);

            SendText sendText = SendText.builder()
                    .text(ERROR_MESSAGE)
                    .chatId(baseMessage.getChat().getChatId())
                    .replyToMessageID(baseMessage.getMessageId())
                    .errorHandler(ImageFetcherBot::handleFatalError)
                    .build();

            imageFetcherBot.getBot().perform(sendText);

            return;

        }

        Item item = imageResults.getRandomItem();

        URL photoUrl;

        try {

            photoUrl = new URL(item.getLink());

        } catch (MalformedURLException ex) {

            handleError(ex, baseMessage);
            return;

        }

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(command.getChat().getChatId())
                .replyToMessageId(baseMessage.getMessageId())
                .photo(new ExternalInputFile(photoUrl))
                .callback(this::consumePhotoMessage)
                .errorHandler(e -> this.handleError(e, baseMessage))
                .build();

        imageFetcherBot.getBot().perform(sendPhoto);

        LOGGER.debug("Successfully sent photo to chat {}.", baseMessage.getChat().getChatId().getId());

    }

    public void handleError(Exception ex, TextMessage baseMessage) {

        ImageFetcherBot.handleError(ex);

        SendText sendText = SendText.builder()
                .text(ERROR_MESSAGE)
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
