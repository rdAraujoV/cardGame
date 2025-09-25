package com.cardgame;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;

class ActionTest {
    private Player playerA;
    private Player playerB;
    private Battlefield battlefield;
    private GameLogic logic;

    @BeforeEach
    void setUp() {
        // 1. Load all card templates
        List<Card> allCards = CardLoader.loadCards();
        // 2. Create decks for the players
        Deck deckA = DeckFactory.createDeck("Metropolis", allCards);
        Deck deckB = DeckFactory.createDeck("Metropolis", allCards);

        // 3. Create players with the new signature
        playerA = Player.createPlayer("TesterA", deckA, 'A');
        playerB = Player.createPlayer("TesterB", deckB, 'B');
        battlefield = new Battlefield();
        logic = new GameLogic();
    }

    @Test
    @DisplayName("Should draw cards from the deck into the player's hand")
    void testDrawHand() {
        // 1. Arrange
        int initialDeckSize = playerA.getDeck().size();
        int cardsToDraw = 3;

        // 2. Act
        playerA.getHand().drawHand(playerA.getDeck(), cardsToDraw);

        // 3. Assert
        assertEquals(cardsToDraw, playerA.getHand().size());
        assertEquals(initialDeckSize - cardsToDraw, playerA.getDeck().size());
    }

    @Test
    @DisplayName("Should play a card from hand to a battlefield row")
    void testUseCard() {
        // 1. Arrange
        playerA.getHand().drawHand(playerA.getDeck(), 1);

        Card cardToPlay = playerA.getHand().getCards().get(0);
        Row targetRow = battlefield.getBackRowA();

        // 2. Act:
        Action.useCard(playerA, cardToPlay, targetRow, logic);

        // 3. Assert:
        assertTrue(targetRow.getCards().contains(cardToPlay));
        assertEquals(playerA.getHand().size(), 0);
        assertTrue(playerA.getPlayedCards().contains(cardToPlay));
    }

    @Test
    @DisplayName("Should not add a card to a row if it's already there from a second useCard call")
    void testUseCardAgain() {
        // 1. Arrange
        playerA.getHand().drawHand(playerA.getDeck(), 1);

        Card cardToPlay = playerA.getHand().getCards().get(0);
        Row targetRow = battlefield.getBackRowA();
        // First call to place the card
        Action.useCard(playerA, cardToPlay, targetRow, logic);

        // 2. Act
        Action.useCard(playerA, cardToPlay, targetRow, logic); // Second call, should do nothing

        // 3. Assert
        assertEquals(1, targetRow.getCards().size(), "The card should not be added to the row a second time.");
        assertEquals(1, playerA.getPlayedCards().size(), "The card should not be added to playedCards a second time.");
    }

    @Test
    @DisplayName("Should not add card to the enemy row")
    void testUseCardEnemyRow() {
        // 1. Arrange
        playerA.getHand().drawHand(playerA.getDeck(), 1);

        Card cardToPlay = playerA.getHand().getCards().get(0);
        Row targetRow = battlefield.getFrontRowB();

        // 2. Act
        Action.useCard(playerA, cardToPlay, targetRow, logic);

        // 3. Assert
        assertFalse(targetRow.getCards().contains(cardToPlay));

    }

    @Test
    @DisplayName("Should move a card from one row to another")
    void testMoveCard() {
        // 1. Arrange
        playerA.getHand().drawHand(playerA.getDeck(), 1);

        Card cardToPlay = playerA.getHand().getCards().get(0);
        Row startingRow = battlefield.getBackRowA();
        Action.useCard(playerA, cardToPlay, startingRow, logic);

        // 2. Act
        Row newRow = battlefield.getMidRowA();
        Action.moveCard(playerA, cardToPlay, newRow);

        // 3. Assert
        assertTrue(newRow.getCards().contains(cardToPlay));
        assertFalse(startingRow.getCards().contains(cardToPlay));
        assertTrue(playerA.getPlayedCards().contains(cardToPlay));
        assertEquals(cardToPlay.getPosition(), newRow);
    }

    @Test
    @DisplayName("Should not move card to the new row")
    void testJumpRow(){
        // 1. Arrange
        playerA.getHand().drawHand(playerA.getDeck(), 1);

        Card cardToPlay = playerA.getHand().getCards().get(0);
        Row startingRow = battlefield.getFrontRowA();
        Action.useCard(playerA, cardToPlay, startingRow, logic);

        // 2. Act
        Row newRow = battlefield.getMidRowB(); // jump rows
        Action.moveCard(playerA, cardToPlay, newRow);

        // 3. Assert
        assertFalse(newRow.getCards().contains(cardToPlay));
    }

    @Test
    @DisplayName("Should deal the correct amount of damage to a target card")
    void testAttackCard_Damage() {
        // 1. Arrange
        Card attackingCard = new Card("Anna", "Desc", Type.MELEE, 5, 3, null, "Set"); // 3 Damage
        playerA.getHand().getCards().add(attackingCard);

        Card targetCard = new Card("George", "Desc", Type.MELEE, 5, 5, null, "Set"); // 5 HP
        playerB.getHand().getCards().add(targetCard);
        int initialTargetHp = targetCard.getHp();

        Action.useCard(playerA, attackingCard, battlefield.getFrontRowA(), logic);
        Action.useCard(playerB, targetCard, battlefield.getFrontRowB(), logic);

        // 2. Act
        Action.attackCard(playerA, playerB, attackingCard, targetCard);

        // 3. Assert
        assertEquals(initialTargetHp - attackingCard.getDamage(), targetCard.getHp());
    }

    @Test
    @DisplayName("Should destroy a card when its HP drops to 0 or less")
    void testAttackCard_Destroy() {
        // 1. Arrange
        Card attackingCard = new Card("Jordan", "Desc", Type.MELEE, 5, 8, null, "Set"); // 8 Damage
        playerA.getHand().getCards().add(attackingCard);

        Card targetCard = new Card("George", "Desc", Type.MELEE, 5, 5, null, "Set"); // 5 HP
        playerB.getHand().getCards().add(targetCard);

        Row targetRow = battlefield.getFrontRowB();
        Action.useCard(playerA, attackingCard, battlefield.getFrontRowA(), logic);
        Action.useCard(playerB, targetCard, targetRow, logic);

        // 2. Act
        Action.attackCard(playerA, playerB, attackingCard, targetCard);

        // 3. Assert
        assertFalse(targetRow.getCards().contains(targetCard));
        assertFalse(playerB.getPlayedCards().contains(targetCard));
    }

    @Test
    @DisplayName("Should instakill card on the same row")
    void testAttackCard_InstaKill() {
        // 1. Arrange
        // Player B attacks Player A
        Card attackingCard = new Card("Jordan", "Desc", Type.MELEE, 5, 1, null, "Set"); // Low damage
        playerB.getHand().getCards().add(attackingCard);

        Card targetCard = new Card("Metropolis City Hall", "Desc", Type.STRUCTURE, 10, 2, null, "Set"); // 10 HP
        playerA.getHand().getCards().add(targetCard);

        Row targetRow = battlefield.getFrontRowB();
        // Both cards are placed on the same row, but owned by different players
        Action.useCard(playerB, attackingCard, targetRow, logic);
        Action.useCard(playerA, targetCard, targetRow, logic);

        // 2. Act
        Action.attackCard(playerB, playerA, attackingCard, targetCard);

        // 3. Assert
        assertFalse(targetRow.getCards().contains(targetCard));
        assertFalse(playerA.getPlayedCards().contains(targetCard), "Target card should be removed from its owner's played cards.");
    }

    @Test
    @DisplayName("Should deal 1 damage to the opponent player if attacking from the correct row")
    void testAttackPlayer() {
        // 1. Arrange

        Card attackingCard = new Card("Anna", "Desc", Type.MELEE, 5, 3, null, "Set");        
        // Manually set the card's state for an isolated test
        playerA.getPlayedCards().add(attackingCard);
        attackingCard.setPosition(battlefield.getBackRowB());

        // 2. Act
        int initialTargetLife = playerB.getLife();
        Action.attackPlayer(playerA, playerB, attackingCard, battlefield);

        // 3. Assert
        assertEquals(initialTargetLife - 1, playerB.getLife());
    }

    @Test
    @DisplayName("Should not deal damage to the opponent player if attacking from an invalid row")
    void testAttackPlayerInvalidPosition() {
        // 1. Arrange
        Card attackingCard = new Card("Anna", "Desc", Type.MELEE, 5, 3, null, "Set");
        playerA.getHand().getCards().add(attackingCard);
        Action.useCard(playerA, attackingCard, battlefield.getMidRowB(), logic);

        // 2. Act
        int initialTargetLife = playerB.getLife();
        Action.attackPlayer(playerA, playerB, attackingCard, battlefield);

        // 3. Assert
        assertEquals(initialTargetLife, playerB.getLife());
    }
}