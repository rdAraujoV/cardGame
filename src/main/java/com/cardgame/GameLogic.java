package com.cardgame;

import java.util.List;

public class GameLogic {

    private Battlefield battlefield;
    private Player playerA;
    private Player playerB;
    private Player activePlayer;
    private int turnCount = 1;
    private boolean playerHasActed = false;

    // constructor
    public GameLogic() {
        this.battlefield = new Battlefield();
        List<Card> allCards = CardLoader.loadCards();
        Deck deckA = DeckFactory.createDeck("Metropolis", allCards);
        this.playerA = Player.createPlayer("Rodrigo", deckA, 'A');
        Deck deckB = DeckFactory.createDeck("Metropolis", allCards);
        this.playerB = Player.createPlayer("Portnoy", deckB, 'B');

        this.playerA.getDeck().shuffle();
        this.playerA.getHand().drawHand(this.playerA.getDeck(), 3);
        this.playerB.getDeck().shuffle();
        this.playerB.getHand().drawHand(this.playerB.getDeck(), 3);

        this.activePlayer = this.playerA;
    }

    // Getters
    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public int getTurnCount(){
        return turnCount;
    }

    public boolean hasPlayerActed() {
        return playerHasActed;
    }

    public boolean isPlayerTurn() {
        return activePlayer == playerA;
    }

    // Setters
    public void setPlayerHasActed(boolean value) {
        this.playerHasActed = value;
    }

    public void setTurnCount(int turnCount){
        this.turnCount = turnCount;
    }

    public void endTurn() {
        if (getWinner() != null)
            return;

        if (activePlayer == playerA) {
            activePlayer = playerB;
        } else {
            activePlayer = playerA;
            playerHasActed = false;
        }
        turnCount++;
    }

    public Player getWinner() {
        if (playerA.getLife() <= 0)
            return playerB;
        if (playerB.getLife() <= 0)
            return playerA;
        return null;
    }

    // methods

    // cant use card on enemy lines on the first turn
    public boolean canPlayCard(Player player, Row targetRow) {
            if (targetRow.getSide() != player.getSide()) {
                return false;
            }
        return true;
    }
}