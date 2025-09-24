package com.cardgame;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class CardLoader {
    public static List<Card> loadCards() {
        Gson gson = new Gson();

        Type cardListType = new TypeToken<List<Card>>() {}.getType();

        try (InputStream inputStream = CardLoader.class.getResourceAsStream("/card.json")) {
            if (inputStream == null) {
                System.err.println("Error: Could not find the file card.json in resources!");
                return Collections.emptyList();
            }

            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            List<Card> cards = gson.fromJson(reader, cardListType);
            System.out.println(cards.size() + " cards loaded successfully!");
            return cards;

        } catch (Exception e) {
            System.err.println("Error reading or processing card.json: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}