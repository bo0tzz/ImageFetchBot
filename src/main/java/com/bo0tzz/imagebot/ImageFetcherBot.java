package com.bo0tzz.imagebot;

import com.bo0tzz.imagebot.client.GoogleImageSearchClient;
import com.bo0tzz.imagebot.handler.ImageCommandHandler;
import com.jtelegram.api.TelegramBot;
import com.jtelegram.api.TelegramBotRegistry;
import com.jtelegram.api.ex.TelegramException;
import com.jtelegram.api.update.PollingUpdateProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

        registry.registerBot(apiKey, (bot, error) -> {

            if (error != null) ImageFetcherBot.handleFatalError(error);

            this.bot = bot;

            this.bot.getCommandRegistry().registerCommand("get", new ImageCommandHandler(this.googleImageSearchClient,this));

        });

    }

    public TelegramBot getBot() {
        return bot;
    }

    private List<String> getKeys() {
        try {
            return Files.lines(new File("keys/key").toPath())
                    .filter((predicate) -> !predicate.equals(""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGiphyKey() {
        try {
            return FileUtils.readFileToString(new File("keys/giphyKey"), "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void handleFatalError(TelegramException ex) {
        LOGGER.error("Fatal error occurred!\n{}", ex.getMessage());
        ex.printStackTrace();
        System.exit(1);
    }

    public static void handleError(TelegramException ex) {
        LOGGER.warn("Fatal error occurred!\n{}", ex.getMessage());
        ex.printStackTrace();
    }

}
