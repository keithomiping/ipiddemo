package com.ipid.demo.utils;

import static com.ipid.demo.constants.Constants.ALPHABET;
import static com.ipid.demo.constants.Constants.BOUND;
import static com.ipid.demo.constants.Constants.LENGTH;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtils {

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(BOUND);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String getRandomCharacters() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(LENGTH);

        for(int i = 0; i < LENGTH; i++)
            sb.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        return sb.toString();
    }
}
