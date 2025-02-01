package com.bridging.weight;

public class Colors {
    // Colors
    public static final String BLACK = "§0";
    public static final String DARK_BLUE = "§1";
    public static final String DARK_GREEN = "§2";
    public static final String DARK_AQUA = "§3";
    public static final String DARK_RED = "§4";
    public static final String DARK_PURPLE = "§5";
    public static final String GOLD = "§6";
    public static final String GRAY = "§7";
    public static final String DARK_GRAY = "§8";
    public static final String BLUE = "§9";
    public static final String GREEN = "§a";
    public static final String AQUA = "§b";
    public static final String RED = "§c";
    public static final String LIGHT_PURPLE = "§d";
    public static final String YELLOW = "§e";
    public static final String WHITE = "§f";

    // Gets color for the speed percentage
    public static String getColor(double pOS) {
        String color = GREEN;
        if (pOS >= 25) {
            color = YELLOW;
        }
        if (pOS >= 50) {
            color = GOLD;
        }
        if (pOS >= 75) {
            color = RED;
        }
        return color;
    }

    // Gets a random color
    public static String getRandomColor() {
        String[] colorsList = {BLACK,DARK_BLUE,DARK_GREEN,DARK_AQUA,DARK_RED,DARK_PURPLE,GOLD,GRAY,DARK_GRAY,BLUE,GREEN,AQUA,RED,LIGHT_PURPLE,YELLOW,WHITE};
        int randomIndex = (int) (Math.random() * colorsList.length);
        return colorsList[randomIndex];
    }

    // Makes a random RGB string (not currently used, tba)
    public static String rgbMaker(int length) {
        String randomLetter = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder finalRGB = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String letter = randomLetter.charAt((int) Math.floor(Math.random() * randomLetter.length())) + "";
            String color = getRandomColor();
            finalRGB.append(color).append(letter);
        }
        return finalRGB.toString();
    }
}