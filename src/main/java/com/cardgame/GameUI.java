package com.cardgame;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.Region;

public class GameUI extends Application {
    // consts
    private static final double CARD_WIDTH = 80;
    private static final double CARD_HEIGHT = 112;
    private static final double PADDING = 10;
    // logic
    private Battlefield battlefield;
    private Player playerA;
    private Player playerB;

    // UI
    private StackPane selectedCardView = null;
    private HBox playerAHandBox;
    private HBox playerBHandBox;
    private GridPane battlefieldGrid;

    private void initializeGameLogic() {
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
    }

    @Override
    public void start(Stage primaryStage) {
        initializeGameLogic();

        // layout principal
        primaryStage.setTitle("Card Game");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1280, 720);

        // battlefield
        this.battlefieldGrid = createBattlefieldView(this.battlefield);
        root.setCenter(this.battlefieldGrid);

        // jogador A
        this.playerAHandBox = createHandView(playerA.getHand().getCards());
        root.setBottom(this.playerAHandBox);
        // jogador B
        this.playerBHandBox = createHandView(playerB.getHand().getCards());
        root.setTop(this.playerBHandBox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createBattlefieldView(Battlefield battlefield) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        final double rowHeight = CARD_HEIGHT + (PADDING * 2);
        for (int i = 0; i < 6; i++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPrefHeight(rowHeight);
            rowConstraint.setMaxHeight(rowHeight);
            grid.getRowConstraints().add(rowConstraint);
        }

        Row[] logicalRows = {
                battlefield.getBackRowB(), battlefield.getMidRowB(), battlefield.getFrontRowB(),
                battlefield.getFrontRowA(), battlefield.getMidRowA(), battlefield.getBackRowA()
        };

        for (int i = 0; i < logicalRows.length; i++) {
            Row currentRowLogic = logicalRows[i];
            HBox rowBox = new HBox();
            rowBox.setSpacing(PADDING);
            rowBox.setPadding(new Insets(PADDING));
            rowBox.setAlignment(Pos.CENTER_LEFT);
            rowBox.setStyle("-fx-border-color: black;");
            rowBox.setUserData(currentRowLogic);

            for (int j = 0; j < 5; j++) {
                Rectangle placeholder = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.LIGHTGRAY);
                placeholder.setStroke(Color.BLACK);
                rowBox.getChildren().add(placeholder);
            }

            rowBox.setOnMouseClicked(event -> {
                if (selectedCardView != null) {
                    Card cardToPlay = (Card) selectedCardView.getUserData();
                    Row targetRow = (Row) rowBox.getUserData();
                    Action.useCard(playerA, cardToPlay, targetRow);
                    updateUI();
                }
            });

            grid.add(rowBox, 0, i);
        }
        return grid;
    }

    private HBox createHandView(List<Card> cards) {
        HBox handBox = new HBox();
        handBox.setSpacing(10);
        handBox.setPadding(new Insets(10));
        handBox.setAlignment(Pos.CENTER);
        handBox.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");

        for (Card card : cards) {
            StackPane cardView = createCardView(card);
            handBox.getChildren().add(cardView);
        }

        return handBox;
    }

    private StackPane createCardView(Card card) {
        StackPane cardView = new StackPane();
        cardView.setPrefSize(CARD_WIDTH, CARD_HEIGHT);

        Rectangle background = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        background.setFill(Color.LIGHTYELLOW);
        background.setStroke(Color.BLACK);
        background.setArcWidth(10);
        background.setArcHeight(10);

        Label nameLabel = new Label(card.getName());
        Label hpLabel = new Label("HP: " + card.getHp());
        Label damageLabel = new Label("DMG: " + card.getDamage());

        StackPane.setAlignment(nameLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(hpLabel, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(damageLabel, Pos.BOTTOM_RIGHT);

        nameLabel.setPadding(new Insets(5));
        hpLabel.setPadding(new Insets(5));
        damageLabel.setPadding(new Insets(5));

        cardView.getChildren().addAll(background, nameLabel, hpLabel, damageLabel);
        cardView.setUserData(card);

        cardView.setOnMouseClicked(event -> {
            if (cardView == selectedCardView) {
                cardView.setTranslateY(0);
                selectedCardView = null;
            } else {
                if (selectedCardView != null) {
                    selectedCardView.setTranslateY(0);
                }
                cardView.setTranslateY(-20);
                selectedCardView = cardView;
            }
        });
        return cardView;
    }

    private void updateUI() {
        playerAHandBox.getChildren().clear();
        for (Card card : playerA.getHand().getCards()) {
            playerAHandBox.getChildren().add(createCardView(card));
        }

        playerBHandBox.getChildren().clear();
        for (int i = 0; i < playerB.getHand().size(); i++) {
            Rectangle cardBack = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.DARKSLATEBLUE);
            cardBack.setStroke(Color.BLACK);
            playerBHandBox.getChildren().add(cardBack);
        }

        for (javafx.scene.Node node : battlefieldGrid.getChildren()) {
            if (node instanceof HBox) {
                HBox rowBox = (HBox) node;
                Row rowLogic = (Row) rowBox.getUserData();

                rowBox.getChildren().clear();

                if (!rowLogic.getCards().isEmpty()) {
                    for (Card card : rowLogic.getCards()) {
                        rowBox.getChildren().add(createCardView(card));
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        Rectangle placeholder = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.LIGHTGRAY);
                        placeholder.setStroke(Color.BLACK);
                        rowBox.getChildren().add(placeholder);
                    }
                }
            }
        }

        if (selectedCardView != null) {
            selectedCardView.setTranslateY(0);
            selectedCardView = null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}