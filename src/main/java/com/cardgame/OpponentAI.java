package com.cardgame;

import java.util.ArrayList;
import java.util.List;

public class OpponentAI {

    public void executeTurn(GameLogic game) {
        System.out.println("Oponente (IA) está pensando...");

        boolean actionTaken = performPlayerAttack(game);

        if (!actionTaken) {
            actionTaken = performSingleAttack(game);
        }

        if (!actionTaken) {
            actionTaken = performMove(game);
        }
        if (!actionTaken) {
            actionTaken = playCard(game);
        }

        System.out.println("Oponente finalizou o turno.");
        game.endTurn();
    }


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

    private boolean performSingleAttack(GameLogic game) {
        Player opponent = game.getPlayerB();
        Player player = game.getPlayerA();
        Card bestAttacker = null;
        Card bestTarget = null;
        int highestTargetScore = -1;

        for (Card attacker : new ArrayList<>(opponent.getPlayedCards())) {
            if (attacker.getDamage() <= 0) {
                continue;
            }

            Card currentTarget = null;

            Row currentRow = attacker.getPosition();
            if (currentRow != null) {
                for (Card potentialTarget : currentRow.getCards()) {
                    if (potentialTarget.getOwner() == player) {
                        currentTarget = potentialTarget;
                        break;
                    }
                }
            }

            if (currentTarget == null) {
                for (Card potentialTarget : player.getPlayedCards()) {
                    int currentScore = potentialTarget.getDamage() + potentialTarget.getHp();
                    if (currentTarget == null || currentScore > (currentTarget.getDamage() + currentTarget.getHp())) {
                        currentTarget = potentialTarget;
                    }
                }
            }

            if (currentTarget != null) {
                int targetScore = currentTarget.getHp() + currentTarget.getDamage();
                if (targetScore > highestTargetScore) {
                    highestTargetScore = targetScore;
                    bestAttacker = attacker;
                    bestTarget = currentTarget;
                }
            }
        }

        if (bestAttacker != null && bestTarget != null) {
            System.out.println("IA: " + bestAttacker.getName() + " ataca " + bestTarget.getName());
            Action.attackCard(opponent, player, bestAttacker, bestTarget);
            return true;
        }
        return false;
    }

    private boolean performMove(GameLogic game) {
        Player opponent = game.getPlayerB();
        Battlefield battlefield = game.getBattlefield();

        for (Card cardToMove : opponent.getPlayedCards()) {
            if (cardToMove.getDamage() > 0) {
                Row currentRow = cardToMove.getPosition();
                Row nextRow = getForwardRow(currentRow, battlefield);

                if (nextRow != null && nextRow.getCards().isEmpty()) {
                    System.out.println(
                            "IA: Movendo " + cardToMove.getName() + " para " + nextRow.getSide() + nextRow.getValue()
                                    + " para avançar.");
                    Action.moveCard(opponent, cardToMove, nextRow);
                    return true; 
                }
            }
        }

        return false; 
    }

    private boolean performPlayerAttack(GameLogic game) {
        Player opponent = game.getPlayerB();
        Player player = game.getPlayerA();
        Battlefield battlefield = game.getBattlefield();

        for (Card attacker : opponent.getPlayedCards()) {
            if (attacker.getPosition() == battlefield.getBackRowA()) {
                System.out.println("IA: " + attacker.getName() + " ataca o jogador diretamente!");
                Action.attackPlayer(opponent, player, attacker, battlefield);
                return true;
            }
        }
        return false;
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
        switch (currentValue) {
            case 6: 
                return battlefield.getMidRowB();
            case 5: 
                return battlefield.getFrontRowB();
            case 4:
                return battlefield.getFrontRowA();
            case 3: 
                return battlefield.getMidRowA();
            case 2: 
                return battlefield.getBackRowA();
            default: 
                return null;
        }
    }
}