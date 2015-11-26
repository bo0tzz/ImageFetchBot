package com.bo0tzz.imagebot;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by bo0tzz
 */
public class Updater implements Runnable {

    ImageBot bot;

    public Updater(ImageBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        File build = new File("build");
        File jar = new File("ImageBot.new");
        int currentBuild = 0;
        int newBuild = 0;

        try {
            currentBuild = Integer.parseInt(FileUtils.readFileToString(build));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                newBuild = Integer.parseInt(Unirest.get("http://ci.zackpollard.pro/job/ImageFetchBot/lastSuccessfulBuild/buildNumber").asString().getBody());
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            if (newBuild > currentBuild) {
                System.out.println("New build found!");
                bot.sendToMazen("New build found! Updating...");
                try {
                    FileUtils.writeStringToFile(build, String.valueOf(newBuild));
                    FileUtils.copyURLToFile(new URL("http://ci.zackpollard.pro/job/ImageFetchBot/lastSuccessfulBuild/artifact/target/ImageBot.jar"), jar);
                    System.out.println("New build downloaded - restarting!");
                    bot.sendToMazen("New build downloaded - restarting!");
                } catch (IOException e) {
                    System.err.println("Updater failed!");
                    e.printStackTrace();
                    break;
                }
                System.exit(0);
            }
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
