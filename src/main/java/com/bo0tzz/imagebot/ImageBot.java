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
        new Thread(new Updater(this)).start();
    }

    public void sendToMazen(String message) {
        bot.getChat(-1001000055116L).sendMessage(message);
    }

    public String[] getKeys() {
        try {
            String[] keys = Files.lines(new File("key").toPath()).toArray(String[]::new);
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
