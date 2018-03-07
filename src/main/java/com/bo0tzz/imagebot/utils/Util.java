package com.bo0tzz.imagebot.utils;

public class Util {

    private Util() {
    }

    public static boolean isEmpty(String string) {

        return null == string || string.equals("");

    }

    public static boolean isNotEmpty(String string) {

        return !isEmpty(string);

    }

}
