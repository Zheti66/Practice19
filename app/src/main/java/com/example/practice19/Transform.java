package com.example.practice19;

public class Transform {
    public static int parseIntOrDefault(String whatParse, int defaultValue) {
        int parse;
        try {
            parse = Integer.parseInt(whatParse);
        } catch (NumberFormatException e) {
            parse = defaultValue;
        }
        return parse;
    }
}