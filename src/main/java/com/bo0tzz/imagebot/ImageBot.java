package com.bo0tzz.imagebot;

import pro.zackpollard.telegrambot.api.TelegramBot;

/**
 * Created by bo0tzz
 */
public class ImageBot {
    public static TelegramBot bot;

    public static void main(String[] args) {
        bot = TelegramBot.login(args[0]);
        bot.getEventsManager().register(new ImageCommandListener());
        bot.startUpdates(false);
    }
}
