package com.bo0tzz.imagebot;

import org.apache.commons.io.FileUtils;
import pro.zackpollard.telegrambot.api.TelegramBot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by bo0tzz
 */
public class ImageBot {
    public static TelegramBot bot;

    public static void main(String[] args) {
        String key = System.getenv("BOT_KEY");
        if (key == null || key.equals("")) {
            if (args.length < 1) {
                System.out.println("Missing auth token.");
                System.exit(0);
            }
            key = args[0];
        }
        new ImageBot().run(key);
    }

    public void run(String key) {
        bot = TelegramBot.login(key);
        bot.getEventsManager().register(new ImageCommandListener(this));
        bot.startUpdates(false);
    }

    public String[] getKeys() {
        try {
            String[] keys = Files.lines(new File("key").toPath())
                    .filter((predicate) -> !predicate.equals(""))
                    .toArray(String[]::new);
            return keys;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGiphyKey() {
        try {
            return FileUtils.readFileToString(new File("giphyKey"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
