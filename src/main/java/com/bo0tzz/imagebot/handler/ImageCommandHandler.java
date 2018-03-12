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
import com.jtelegram.api.message.Message;
import com.jtelegram.api.message.impl.TextMessage;
import com.jtelegram.api.message.input.file.ExternalInputFile;
import com.jtelegram.api.requests.framework.TelegramRequest;
import com.jtelegram.api.requests.message.send.SendPhoto;
import com.jtelegram.api.requests.message.send.SendText;
import com.jtelegram.api.requests.message.send.SendVideo;
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

        LOGGER.debug("Received new image search command: \"{}\" from user {} in chat {}.", query, baseMessage.getFrom().getUsernameFallbackName(), baseMessage.getChat().getChatId().getId());

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

        TelegramRequest request;

        switch (item.getMime()) {

            case "image/jpeg":
            case "image/jpg":
            case "image/png":
                request = SendPhoto.builder()
                        .chatId(command.getChat().getChatId())
                        .replyToMessageId(baseMessage.getMessageId())
                        .photo(new ExternalInputFile(photoUrl))
                        .callback(this::consumeMessage)
                        .errorHandler(e -> this.handleError(e, baseMessage))
                        .build();
                break;

            case "image/gif":
                request = SendVideo.builder()
                        .chatId(command.getChat().getChatId())
                        .replyToMessageId(baseMessage.getMessageId())
                        .video(new ExternalInputFile(photoUrl))
                        .callback(this::consumeMessage)
                        .errorHandler(e -> this.handleError(e, baseMessage))
                        .build();
                break;

            default:
                return;

        }

        imageFetcherBot.getBot().perform(request);

        LOGGER.debug("Successfully sent photo {} to chat {}.", photoUrl, baseMessage.getChat().getChatId().getId());

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

    public void consumeMessage(Message message) {

        LOGGER.trace("Sent message {} to chat {}", message.getMessageId(), message.getChat().getChatId().getId());

    }

}
