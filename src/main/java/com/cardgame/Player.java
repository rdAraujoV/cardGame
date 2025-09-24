package com.cardgame;
import java.util.*;

public class Player {
    //attributes
    private String name;
    private Battlefield battlefield;
    private Deck deck;
    private Hand hand;
    private int life;
    private List<Card> playedCards;
    private char side;

    //getters
    public String getName() {
        return name;
    }

    public Battlefield getBattlefield(){
        return battlefield;
    }
    public Deck getDeck(){
        return deck;
    }
    public Hand getHand(){
        return hand;
    }
    public int getLife(){
        return life;
    }
    public List<Card> getPlayedCards(){
        return playedCards;
    }
    public char getSide(){
        return side;
    }


    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setBattlefield(Battlefield battlefield){
        this.battlefield = battlefield;
    }
    public void setDeck(Deck deck){
        this.deck = deck;
    }
    public void setHand(Hand hand){
        this.hand = hand;
    }
    public void setLife(int life){
        this.life = life;
    }
    public void setPlayedCards(List<Card> playedCards){
        this.playedCards = playedCards;
    }
    public void setSide(char side){
        this.side = side;
    }


    //methods
    public static Player createPlayer(String name, Deck deck, char side) {
        Player player = new Player();
        player.setName(name);
        player.setLife(2);
        player.setPlayedCards(new ArrayList<>());
        player.setSide(side);
        player.setDeck(deck);
        player.setHand(new Hand());
        player.setBattlefield(new Battlefield());

        return player;
    }
}