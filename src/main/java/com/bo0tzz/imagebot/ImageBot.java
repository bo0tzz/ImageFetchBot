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
        if (args.length < 1) {
            System.out.println("Missing auth token.");
            System.exit(0);
        }
        new ImageBot().run(args);
    }

    public void run(String[] args) {
        bot = TelegramBot.login(args[0]);
        bot.getEventsManager().register(new ImageCommandListener(this));
        bot.startUpdates(false);
    }

    public void sendToMazen(String message) {
        TelegramBot.getChat(-17349250).sendMessage(message, this.bot);
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
