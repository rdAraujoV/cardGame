package com.cardgame;
import java.util.ArrayList;

public class Row {
    private ArrayList <Card> cards = new ArrayList<>();

    // getters
    public ArrayList<Card> getCards() {
        return cards;
    }

    // setters
    public void setCards(ArrayList<Card> cards){
        this.cards = cards;
    }

    public void addCard(Card card){
        cards.add(card);
    }
}