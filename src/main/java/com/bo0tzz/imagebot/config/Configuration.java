package com.bo0tzz.imagebot.config;

import com.bo0tzz.imagebot.ImageFetcherBot;
import com.bo0tzz.imagebot.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration {

    private final String telegramKey;
    private final List<String> googleKeys;

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    public Configuration(String[] args) {

        this.telegramKey = getTelegramKey(args);
        this.googleKeys = getGoogleKeysFromFilesystem();

    }

    private String getTelegramKey(String[] args) {
        String apiKey = System.getenv("BOT_KEY");
        if (apiKey == null || apiKey.equals("")) {
            if (args.length < 1) {
                LOGGER.error("Missing auth token");
                System.exit(0);
            }
            apiKey = args[0];
        }
        return apiKey;
    }

    private List<String> getGoogleKeysFromFilesystem() {
        try {
            return Files.lines(new File("keys/key").toPath())
                    .filter(Util::isNotEmpty)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            ImageFetcherBot.handleError(e);
            return new LinkedList<>();
        }
    }

    public String getTelegramKey() {
        return telegramKey;
    }

    public List<String> getGoogleKeys() {
        return googleKeys;
    }
}
