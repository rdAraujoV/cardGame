package com.cardgame;
import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cards = new ArrayList<>();

    public void drawHand(Deck fromDeck, int cardCount) {
        for (int i = 0; i < cardCount; i++) {
            Card drawnCard = fromDeck.draw();
            if (drawnCard != null) {
                cards.add(drawnCard);
            }
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public int size() {
        return cards.size();
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }
}