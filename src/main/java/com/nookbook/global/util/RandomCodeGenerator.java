package com.nookbook.global.util;


import java.security.SecureRandom;

public class RandomCodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    // exclude 1, I and 0, O to avoid confusion
    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String NUM_POOL = "23456789";

    // only numbers
    public static String generateRandomNum(int length) {
        int maxValue = (int) Math.pow(10, length);
        return String.format("%0" + length + "d", secureRandom.nextInt(maxValue));
    }

    // numbers and uppercase letters
    public static String generateCode(int length) {

        int half = length / 2;
        StringBuilder sb = new StringBuilder(length);

        // 1. char 3
        for (int i = 0; i < half; i++) {
            int idx = secureRandom.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(idx));
        }

        // 2. num 3
        for (int i = 0; i < half; i++) {
            int idx = secureRandom.nextInt(NUM_POOL.length());
            sb.append(NUM_POOL.charAt(idx));
        }

        // 3. shuffle
        char[] chars = sb.toString().toCharArray();

        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);

            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }
}