package com.bo0tzz.imagebot;

import com.bo0tzz.imagebot.client.GoogleImageSearchClient;
import com.bo0tzz.imagebot.config.Configuration;
import com.bo0tzz.imagebot.handler.ImageCommandHandler;
import com.bo0tzz.imagebot.handler.InlineQueryHandler;
import com.jtelegram.api.TelegramBot;
import com.jtelegram.api.TelegramBotRegistry;
import com.jtelegram.api.events.inline.InlineQueryEvent;
import com.jtelegram.api.ex.TelegramException;
import com.jtelegram.api.update.PollingUpdateProvider;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bo0tzz
 */
public class ImageFetcherBot {

    private TelegramBot bot;

    private final GoogleImageSearchClient googleImageSearchClient;
    private final Configuration configuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageFetcherBot.class);


    public static void main(String[] args) {
        BasicConfigurator.configure();
        new ImageFetcherBot(args);
    }

    public ImageFetcherBot(String[] args) {

        this.configuration = new Configuration(args);
        this.googleImageSearchClient = new GoogleImageSearchClient(configuration.getGoogleKeys());

        TelegramBotRegistry registry = TelegramBotRegistry.builder()
                .updateProvider(new PollingUpdateProvider())
                .eventThreadCount(10)
                .build();

        registry.registerBot(configuration.getTelegramKey(), this::setupTelegramBot);

    }

    public TelegramBot getBot() {
        return bot;
    }

    public static void handleFatalError(Exception ex) {
        LOGGER.error("Fatal error occurred!", ex);
        System.exit(1);
    }

    public static void handleError(Exception ex) {
        LOGGER.warn("Error occurred!", ex);
    }

    private void setupTelegramBot(TelegramBot telegramBot, TelegramException error) {
        if (error != null) ImageFetcherBot.handleFatalError(error);

        this.bot = telegramBot;

        this.bot.getCommandRegistry().registerCommand(
                "get",
                new ImageCommandHandler(this.googleImageSearchClient, this));
        this.bot.getEventRegistry().registerEvent(
                InlineQueryEvent.class,
                new InlineQueryHandler(this.googleImageSearchClient, this));
    }
}
