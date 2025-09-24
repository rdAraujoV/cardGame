package com.cardgame;

import java.util.List;
import java.util.stream.Collectors;

public class DeckFactory {

    public static Deck createDeck(String setName, List<Card> allCards) {
        Deck deck = new Deck();
        deck.setName(setName + " Deck");

        List<Card> cardsForDeck = allCards.stream()
                .filter(card -> setName.equalsIgnoreCase(card.getSet()))
                .map(cardTemplate -> new Card(cardTemplate))
                .collect(Collectors.toList());
        
        deck.setCards(cardsForDeck);
        
        System.out.println("Deck '" + deck.getName() + "' created with " + deck.size() + " cards.");
        return deck;
    }
}