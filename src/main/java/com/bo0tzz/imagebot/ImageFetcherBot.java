package com.bo0tzz.imagebot;

import com.bo0tzz.imagebot.client.GoogleImageSearchClient;
import com.bo0tzz.imagebot.handler.ImageCommandHandler;
import com.bo0tzz.imagebot.handler.InlineQueryHandler;
import com.bo0tzz.imagebot.utils.Util;
import com.jtelegram.api.TelegramBot;
import com.jtelegram.api.TelegramBotRegistry;
import com.jtelegram.api.events.inline.InlineQueryEvent;
import com.jtelegram.api.update.PollingUpdateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bo0tzz
 */
public class ImageFetcherBot {

    private TelegramBot bot;
    private final GoogleImageSearchClient googleImageSearchClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageFetcherBot.class);


    public static void main(String[] args) {
        new ImageFetcherBot(args);
    }

    public ImageFetcherBot(String[] args) {

        String apiKey = System.getenv("BOT_KEY");
        if (apiKey == null || apiKey.equals("")) {
            if (args.length < 1) {
                LOGGER.error("Missing auth token");
                System.exit(0);
            }
            apiKey = args[0];
        }

        this.googleImageSearchClient = new GoogleImageSearchClient(this.getKeys());

        TelegramBotRegistry registry = TelegramBotRegistry.builder()
                .updateProvider(new PollingUpdateProvider())
                .build();

        registry.registerBot(apiKey, (telegramBot, error) -> {

            if (error != null) ImageFetcherBot.handleFatalError(error);

            this.bot = telegramBot;

            this.bot.getCommandRegistry().registerCommand("get", new ImageCommandHandler(this.googleImageSearchClient,this));
            this.bot.getEventRegistry().registerEvent(InlineQueryEvent.class, new InlineQueryHandler(this.googleImageSearchClient, this));

        });

    }

    public TelegramBot getBot() {
        return bot;
    }

    private List<String> getKeys() {
        try {
            return Files.lines(new File("keys/key").toPath())
                    .filter(Util::isNotEmpty)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            ImageFetcherBot.handleError(e);
            return new LinkedList<>();
        }
    }

    public static void handleFatalError(Exception ex) {
        LOGGER.error("Fatal error occurred!", ex);
        System.exit(1);
    }

    public static void handleError(Exception ex) {
        LOGGER.warn("Error occurred!", ex);
    }

}
