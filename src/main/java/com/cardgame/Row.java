package com.cardgame;

import java.util.ArrayList;

public class Row {
    private ArrayList<Card> cards = new ArrayList<>();
    private String name;
    private char side;
    private int value;

    // getters
    public ArrayList<Card> getCards() {
        return cards;
    }

    public char getSide() {
        return side;
    }
    public int getValue(){
        return value;
    }
    public String getName(){
        return name;
    }

    // setters
    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void setSide(char side) {
        this.side = side;
    }

    public void setValue(int value){
        this.value = value;
    }
    public void setName(String name){
        this.name = name;
    }

    // constructor
    public Row(char side, int value, String name) {
        this.side = side;
        this.value = value;
        this.name = name;
    }

    public void addCard(Card card) {
        cards.add(card);
    }
}