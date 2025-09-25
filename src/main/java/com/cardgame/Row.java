package com.cardgame;

import java.util.ArrayList;

public class Row {
    private ArrayList<Card> cards = new ArrayList<>();
    private char side;

    // getters
    public ArrayList<Card> getCards() {
        return cards;
    }

    public char getSide() {
        return side;
    }

    // setters
    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void setSide(char side) {
        this.side = side;
    }

    // constructor
    public Row(char side) {
        this.side = side;
    }

    public void addCard(Card card) {
        cards.add(card);
    }
}