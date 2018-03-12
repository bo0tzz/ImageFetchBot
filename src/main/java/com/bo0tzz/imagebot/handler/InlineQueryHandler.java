package com.bo0tzz.imagebot.handler;

import com.bo0tzz.imagebot.ImageFetcherBot;
import com.bo0tzz.imagebot.client.GoogleImageSearchClient;
import com.bo0tzz.imagebot.google.GoogleSearchResponse;
import com.bo0tzz.imagebot.google.Item;
import com.bo0tzz.imagebot.google.error.GoogleError;
import com.bo0tzz.imagebot.utils.Util;
import com.jtelegram.api.events.EventHandler;
import com.jtelegram.api.events.inline.InlineQueryEvent;
import com.jtelegram.api.inline.input.InputTextMessageContent;
import com.jtelegram.api.inline.result.InlineResultArticle;
import com.jtelegram.api.inline.result.InlineResultGif;
import com.jtelegram.api.inline.result.InlineResultPhoto;
import com.jtelegram.api.inline.result.framework.InlineResult;
import com.jtelegram.api.requests.inline.AnswerInlineQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InlineQueryHandler implements EventHandler<InlineQueryEvent> {

    private final GoogleImageSearchClient googleImageSearchClient;
    private final ImageFetcherBot imageFetcherBot;

    private static final Logger LOGGER = LoggerFactory.getLogger(InlineQueryHandler.class);

    public static final String ERROR_MESSAGE = "I encountered an error while trying to find your image!%n" +
            "Please try again. If this keeps happening, please contact my creator @bo0tzz and mention error code %s";

    public InlineQueryHandler(GoogleImageSearchClient googleImageSearchClient, ImageFetcherBot imageFetcherBot) {

        this.googleImageSearchClient = googleImageSearchClient;
        this.imageFetcherBot = imageFetcherBot;

    }

    @Override
    public void onEvent(InlineQueryEvent inlineQueryEvent) {

        //TODO wait for a full query before sending request to google?

        UUID queryId = UUID.randomUUID();
        MDC.put("queryId", queryId.toString());

        String query = inlineQueryEvent.getQuery().getQuery();

        if (Util.isEmpty(query)) {

            String message = "Please send a query to search for! Example:\n@ImageFetcherBot dogs";

            InlineResultArticle article = InlineResultArticle.builder()
                    .id(queryId.toString())
                    .title(message)
                    .description(message)
                    .inputMessageContent(InputTextMessageContent.builder()
                            .messageText(message)
                            .build())
                    .build();

            AnswerInlineQuery answer = AnswerInlineQuery.builder()
                    .addResult(article)
                    .cacheTime(604800)
                    .queryId(queryId.toString())
                    .build();

            imageFetcherBot.getBot().perform(answer);

            return;

        }

        LOGGER.debug("Received new inline query: [{}] from user [{}].", query, inlineQueryEvent.getQuery().getFrom().getUsername());

        GoogleSearchResponse imageResults = googleImageSearchClient.getImageResults(query);

        LOGGER.debug("Received google response for query: \"{}\"", query);

        if (imageResults.hasError()) {

            GoogleError error = imageResults.getError();
            LOGGER.error("Received an error response from google: {}", error);

            InlineResultArticle article = InlineResultArticle.builder()
                    .id(queryId.toString())
                    .title("An error occurred!")
                    .description("Google returned an error response!")
                    .inputMessageContent(InputTextMessageContent.builder()
                            .messageText(String.format(ERROR_MESSAGE, queryId.toString()))
                            .build())
                    .build();

            AnswerInlineQuery answer = AnswerInlineQuery.builder()
                    .addResult(article)
                    .cacheTime(0)
                    .queryId(queryId.toString())
                    .build();

            imageFetcherBot.getBot().perform(answer);

            return;

        }

        LOGGER.debug("Creating photo response list");

        List<InlineResult> resultPhotos = imageResults.getItems().stream()
                .map(this::toInlineResult)
                .collect(Collectors.toList());

        LOGGER.debug("Preparing inline query response");

        AnswerInlineQuery answer = AnswerInlineQuery.builder()
                .queryId(inlineQueryEvent.getQuery().getId())
                .addAllResults(resultPhotos)
                .errorHandler(ImageFetcherBot::handleError) //TODO add callback for success flow
                .cacheTime(604800)
                .build();

        LOGGER.debug("Performing inline query response");

        imageFetcherBot.getBot().perform(answer);

        LOGGER.debug("Sent inline query response!");

        MDC.clear();

    }

    public InlineResult toInlineResult(Item item) {

        //TODO Handle this in the Item class
        //TODO using gson adapters
        switch (item.getMime()) {

            case "image/gif":
                LOGGER.debug("Image type was image/gif");
                return InlineResultGif.builder()
                        .id(UUID.randomUUID().toString())
                        .url(item.getLink())
                        .thumbUrl(item.getImage().getThumbnailLink())
                        .build();

            case "image/jpeg":
            case "image/jpg":
            case "image/png":
                LOGGER.debug("Image type was {}", item.getMime());
                return InlineResultPhoto.builder()
                        .id(UUID.randomUUID().toString()) //TODO figure out how to do something useful here
                        .url(item.getLink())
                        .thumbUrl(item.getImage().getThumbnailLink())
                        .build();

            default:
                LOGGER.warn("Attempted to send unknown image type [{}], image url is [{}]", item.getMime(), item.getLink());
                return null;

        }

    }

}
