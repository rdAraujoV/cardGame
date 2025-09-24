package com.cardgame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();
    private String name;

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }
    // getters
    public List<Card> getCards() {
        return cards;
    }
    public String getName() {
        return name;
    }

    // setters

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setName(String name){
        this.name = name;
    }
}