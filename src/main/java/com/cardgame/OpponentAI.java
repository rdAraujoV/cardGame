package com.cardgame;

import java.util.ArrayList;
import java.util.List;

public class OpponentAI {

    public void executeTurn(GameLogic game) {
        System.out.println("Oponente (IA) está pensando...");

        // The AI should only perform ONE action per turn.
        // We will prioritize attacking. If no good attack is found, we play a card.
        boolean actionTaken = performPlayerAttack(game);

        // If no attack was made, try to play a card
        if (!actionTaken) {
            actionTaken = performSingleAttack(game);
        }

        // If no attack was made, try to move a card to a better position
        if (!actionTaken) {
            actionTaken = performMove(game);
        }
        if (!actionTaken) {
            actionTaken = playCard(game);
        }

        System.out.println("Oponente finalizou o turno.");
        game.endTurn();
    }

    /**
     * Finds the best card in hand and plays it to an available row.
     * This is considered a single action.
     */
    private boolean playCard(GameLogic game) {
        Player opponent = game.getPlayerB();
        List<Card> hand = opponent.getHand().getCards();

        if (hand.isEmpty()) {
            System.out.println("Oponente não tem cartas para jogar.");
            return false;
        }

        Card bestCardToPlay = null;
        int highestScore = -1;

        for (Card card : hand) {
            int currentScore = card.getDamage() + card.getHp();
            if (currentScore > highestScore) {
                highestScore = currentScore;
                bestCardToPlay = card;
            }
        }

        if (bestCardToPlay != null) {
            Row targetRow = findEmptyRowFor(game.getBattlefield(), opponent);
            if (targetRow != null) {
                System.out.println("Oponente joga: " + bestCardToPlay.getName());
                Action.useCard(opponent, bestCardToPlay, targetRow, game);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the single best attack to make and executes it.
     * 
     * @return true if an attack was made, false otherwise.
     */
    private boolean performSingleAttack(GameLogic game) {
        Player opponent = game.getPlayerB();
        Player player = game.getPlayerA();
        Card bestAttacker = null;
        Card bestTarget = null;
        int highestTargetScore = -1;

        // Find the best possible attack
        for (Card attacker : new ArrayList<>(opponent.getPlayedCards())) {
            // AI should not attack with cards that have no damage
            if (attacker.getDamage() <= 0) {
                continue;
            }

            Card currentTarget = null;

            // Priority 1: Find insta-kill target on the SAME row
            Row currentRow = attacker.getPosition();
            if (currentRow != null) {
                for (Card potentialTarget : currentRow.getCards()) {
                    if (potentialTarget.getOwner() == player) { // Found an enemy on the same row
                        currentTarget = potentialTarget;
                        break; // Insta-kill found, this is the best target for this attacker
                    }
                }
            }

            // Priority 2: If no insta-kill, find the strongest enemy card anywhere
            if (currentTarget == null) {
                for (Card potentialTarget : player.getPlayedCards()) {
                    int currentScore = potentialTarget.getDamage() + potentialTarget.getHp();
                    // Simple logic: just pick the one with the highest score
                    if (currentTarget == null || currentScore > (currentTarget.getDamage() + currentTarget.getHp())) {
                        currentTarget = potentialTarget;
                    }
                }
            }

            // If we found a target, see if it's better than our current best option
            if (currentTarget != null) {
                int targetScore = currentTarget.getHp() + currentTarget.getDamage();
                if (targetScore > highestTargetScore) {
                    highestTargetScore = targetScore;
                    bestAttacker = attacker;
                    bestTarget = currentTarget;
                }
            }
        }

        // Execute the best attack found
        if (bestAttacker != null && bestTarget != null) {
            System.out.println("IA: " + bestAttacker.getName() + " ataca " + bestTarget.getName());
            Action.attackCard(opponent, player, bestAttacker, bestTarget);
            return true;
        }
        return false;
    }

    /**
     * Finds a useful move to make and executes it.
     * A good move is positioning a card for a future player attack.
     * @return true if a move was made, false otherwise.
     */
    private boolean performMove(GameLogic game) {
        Player opponent = game.getPlayerB();
        Battlefield battlefield = game.getBattlefield();

        // Find a card that can attack and is not already in the target row
        for (Card cardToMove : opponent.getPlayedCards()) {
            if (cardToMove.getDamage() > 0) {
                Row currentRow = cardToMove.getPosition();
                // Determine the next row to move forward to (towards player A)
                Row nextRow = getForwardRow(currentRow, battlefield);

                // A good move is advancing into an completely empty row.
                if (nextRow != null && nextRow.getCards().isEmpty()) {
                    System.out.println(
                            "IA: Movendo " + cardToMove.getName() + " para " + nextRow.getSide() + nextRow.getValue()
                                    + " para avançar.");
                    Action.moveCard(opponent, cardToMove, nextRow);
                    return true; // A move was made
                }
            }
        }

        return false; // No useful move was found
    }

    /**
     * Checks if any card is in position to attack the player directly and does so.
     * @return true if a player attack was made, false otherwise.
     */
    private boolean performPlayerAttack(GameLogic game) {
        Player opponent = game.getPlayerB();
        Player player = game.getPlayerA();
        Battlefield battlefield = game.getBattlefield();

        // Find a card in position to attack the player
        for (Card attacker : opponent.getPlayedCards()) {
            // For the AI (Player B), the target row is the player's (Player A) back row
            if (attacker.getPosition() == battlefield.getBackRowA()) {
                System.out.println("IA: " + attacker.getName() + " ataca o jogador diretamente!");
                Action.attackPlayer(opponent, player, attacker, battlefield);
                return true; // Attack was made
            }
        }
        return false; // No card was in position
    }


    private Row findEmptyRowFor(Battlefield battlefield, Player player) {
        if (player.getSide() == 'B') {
            if (battlefield.getFrontRowB().getCards().size() < 5)
                return battlefield.getFrontRowB();
            if (battlefield.getMidRowB().getCards().size() < 5)
                return battlefield.getMidRowB();
            if (battlefield.getBackRowB().getCards().size() < 5)
                return battlefield.getBackRowB();
        }
        return null;
    }

    private Row getForwardRow(Row currentRow, Battlefield battlefield) {
        int currentValue = currentRow.getValue();
        // For Player B, moving "forward" means decreasing the row value.
        switch (currentValue) {
            case 6: // BackRowB
                return battlefield.getMidRowB();
            case 5: // MidRowB
                return battlefield.getFrontRowB();
            case 4: // FrontRowB
                return battlefield.getFrontRowA();
            case 3: // FrontRowA
                return battlefield.getMidRowA();
            case 2: // MidRowA
                return battlefield.getBackRowA();
            default: // Already at the end (BackRowA) or invalid row
                return null;
        }
    }
}