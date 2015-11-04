package com.bo0tzz.imagebot;

import org.apache.commons.io.FileUtils;
import pro.zackpollard.telegrambot.api.TelegramBot;

import java.io.File;
import java.io.IOException;

/**
 * Created by bo0tzz
 */
public class ImageBot {
    public static TelegramBot bot;

    public static void main(String[] args) {
        new ImageBot().run(args);
    }

    public void run(String[] args) {
        bot = TelegramBot.login(args[0]);
        bot.getEventsManager().register(new ImageCommandListener(this));
        bot.startUpdates(false);
    }

    public String getKey() {
        try {
            String key = FileUtils.readFileToString(new File("key"));
            return key;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
