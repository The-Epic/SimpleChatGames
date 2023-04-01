package me.epic.chatgames.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStringGenerator {

    private String characters;
    private final Random random;


    public RandomStringGenerator(String characters) {
        this.characters = characters;
        this.random = ThreadLocalRandom.current();
    }


    public String generate(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be positive");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

}
