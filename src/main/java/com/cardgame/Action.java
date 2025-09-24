package com.cardgame;

public class Action {

    // methods

    public static void useCard(Player player, Card card, Row chosenRow) {
        
        if (player.getPlayedCards().contains(card)) {
            return;
        }
        player.getHand().removeCard(card);
        card.setOwner(player); 
        chosenRow.addCard(card);
        card.setPosition(chosenRow);
        player.getPlayedCards().add(card);
    }
    public static void moveCard(Player player, Card card, Row newRow){
        if (!player.getPlayedCards().contains(card)) {
            return;
        }
        
        Row oldRow = card.getPosition();
        if (oldRow != null) {
            oldRow.getCards().remove(card);
        }
        
        newRow.addCard(card);
        card.setPosition(newRow);
    }

    public static void attackCard(Player attacker, Player target, Card attackingCard, Card targetCard) {
        if (!attacker.getPlayedCards().contains(attackingCard)) {
            return;
        }
        if (!target.getPlayedCards().contains(targetCard)) {
            return;
        }

        int damage = attackingCard.getDamage();
        int targetHp = targetCard.getHp();
        targetHp -= damage;

        targetCard.setHp(targetHp);

        if (targetHp <= 0) {
            targetCard.getPosition().getCards().remove(targetCard);
            target.getPlayedCards().remove(targetCard);
        }
    }

    public static void attackPlayer(Player attacker, Player target, Card attackingCard, Battlefield battlefield) {
        if (!attacker.getPlayedCards().contains(attackingCard)) {
            return;
        }

        // A card can attack the opponent directly if it's in the opponent's back row.
        boolean canAttackPlayer = (attacker.getSide() == 'A' && attackingCard.getPosition() == battlefield.getBackRowB())
                || (attacker.getSide() == 'B' && attackingCard.getPosition() == battlefield.getBackRowA());

        if (canAttackPlayer) {
            int damage = 1;
            int newLife = target.getLife() - damage;
            target.setLife(Math.max(0, newLife));
        }
    }
}