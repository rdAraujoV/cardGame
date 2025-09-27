package com.cardgame;

import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameUI extends Application {
    private double cardWidth = 100;
    private double cardHeight = 125;
    private GameLogic game;
    private OpponentAI opponentAI;

    private StackPane selectedCardView = null;
    private StackPane attackingCardView = null;
    private HBox playerAHandBox;
    private GridPane battlefieldGrid;
    private Circle playerAIndicator, playerBIndicator;
    private Label playerALifeLabel, playerBLifeLabel;
    private Button endTurnButton;

    @Override
    public void start(Stage primaryStage) {
        this.game = new GameLogic();
        this.opponentAI = new OpponentAI();
        primaryStage.setTitle("Card Game");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1280, 720);

        try {
            Image backgroundImage = new Image(getClass().getResourceAsStream("/assets/background.png"));
            BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
            root.setBackground(new Background(bgImage));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar a imagem de fundo. Verifique o caminho do arquivo.");
        }
        this.battlefieldGrid = createBattlefieldView(game.getBattlefield());
        root.setCenter(this.battlefieldGrid);

        this.playerALifeLabel = new Label();
        VBox playerAView = createPlayerView(game.getPlayerA(), this.playerALifeLabel);
        this.endTurnButton = new Button("End Turn");
        this.endTurnButton.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.6); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #5C4033; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8;");
        this.endTurnButton.setOnMouseEntered(e -> endTurnButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #5C4033; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8;"));
        this.endTurnButton.setOnMouseExited(e -> endTurnButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #5C4033; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8;"));
        this.endTurnButton.setOnAction(event -> endTurn());
        this.playerAHandBox = createHandView(game.getPlayerA().getHand().getCards());
        this.playerAIndicator = new Circle(15, Color.GOLD);
        this.playerAIndicator.setStroke(Color.BLACK);
        HBox bottomContainer = new HBox(30, playerAView, this.playerAHandBox, this.endTurnButton, this.playerAIndicator);
        bottomContainer.setPadding(new Insets(10));
        bottomContainer.setAlignment(Pos.CENTER);
        root.setBottom(bottomContainer);

        this.playerBLifeLabel = new Label();
        VBox playerBView = createPlayerView(game.getPlayerB(), this.playerBLifeLabel);
        this.playerBIndicator = new Circle(15, Color.GOLD);
        this.playerBIndicator.setStroke(Color.BLACK);
        HBox topContainer = new HBox(30, playerBView, this.playerBIndicator);
        topContainer.setPadding(new Insets(10));
        topContainer.setAlignment(Pos.CENTER);
        root.setTop(topContainer);

        playerBView.setOnMouseClicked(event -> {
            if (game.isPlayerTurn() && !game.hasPlayerActed() && attackingCardView != null) {
                Card attackingCard = (Card) attackingCardView.getUserData();
                Action.attackPlayer(game.getActivePlayer(), game.getPlayerB(), attackingCard, game.getBattlefield());
                game.setPlayerHasActed(true);
                updateUI();
            }
        });

        scene.widthProperty().addListener((obs, oldVal, newVal) -> handleResize(scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> handleResize(scene));

        handleResize(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
        updateUI();
    }

    private void endTurn() {
        game.endTurn();
        updateUI();

        if (!game.isPlayerTurn() && game.getWinner() == null) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> executeOpponentTurn());
            pause.play();
        }
    }

    private void handleResize(Scene scene) {
        this.cardHeight = scene.getHeight() * 0.10;
        this.cardWidth = this.cardHeight * 0.8;

        updateUI();
    }

    private void updateUI() {
        playerAHandBox.getChildren().clear();
        for (Card card : game.getPlayerA().getHand().getCards()) {
            playerAHandBox.getChildren().add(createCardView(card, game.getPlayerA()));
        }

        for (javafx.scene.Node node : battlefieldGrid.getChildren()) {
            if (node instanceof HBox) {
                HBox rowBox = (HBox) node;
                Row rowLogic = (Row) rowBox.getUserData();
                rowBox.getChildren().clear();

                for (Card card : rowLogic.getCards()) {
                    rowBox.getChildren().add(createCardView(card, card.getOwner()));
                }

                // 2. Preenche o restante com placeholders até o máximo de 5
                int remainingSlots = 5 - rowLogic.getCards().size();
                for (int i = 0; i < remainingSlots; i++) {
                    rowBox.getChildren().add(createPlaceholder(rowLogic));
                }
            }
        }

        playerALifeLabel.setText("Vida: " + game.getPlayerA().getLife());
        playerBLifeLabel.setText("Vida: " + game.getPlayerB().getLife());

        // Estilo do indicador de turno
        String activeCoinStyle = "-fx-fill: radial-gradient(center 50% 50%, radius 50%, #FFD700, #B8860B); -fx-stroke: black; -fx-stroke-width: 1.5;";
        String inactiveCoinStyle = "-fx-fill: radial-gradient(center 50% 50%, radius 50%, #696969, #2F4F4F); -fx-stroke: black; -fx-stroke-width: 1;";
        if (game.isPlayerTurn()) {
            playerAIndicator.setStyle(activeCoinStyle);
            playerBIndicator.setStyle(inactiveCoinStyle);
        } else {
            playerAIndicator.setStyle(inactiveCoinStyle);
            playerBIndicator.setStyle(activeCoinStyle);
        }

        if (selectedCardView != null)
            selectedCardView.setTranslateY(0);
        if (attackingCardView != null)
            attackingCardView.setStyle("-fx-border-color: black;");
        selectedCardView = null;
        clearRowHighlights();
        attackingCardView = null;

        Player winner = game.getWinner();
        if (winner != null) {
            showGameOverAlert("Vitória de " + winner.getName() + "!");
        }
    }

    private VBox createPlayerView(Player player, Label lifeLabel) {
        VBox playerBox = new VBox(10);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(15));
        playerBox.setPrefWidth(150);
        playerBox.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.4); " +
                        "-fx-border-color: #5C4033; " +
                        "-fx-border-width: 2; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10;");

        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        lifeLabel.setText("Vida: " + player.getLife());
        lifeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFD700;");
        playerBox.getChildren().addAll(nameLabel, lifeLabel);
        return playerBox;
    }

    private GridPane createBattlefieldView(Battlefield battlefield) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        grid.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7); " +
                        "-fx-border-color: #5C4033; " +
                        "-fx-border-width: 3; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15;");
        grid.setMaxWidth((cardWidth * 5) + (10 * 4) + 20);

        final double rowHeight = cardHeight + 4;
        for (int i = 0; i < 6; i++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPrefHeight(rowHeight);
            rowConstraint.setMaxHeight(rowHeight);
        }

        Row[] logicalRows = {
                battlefield.getBackRowB(), battlefield.getMidRowB(), battlefield.getFrontRowB(),
                battlefield.getFrontRowA(), battlefield.getMidRowA(), battlefield.getBackRowA()
        };

        for (int i = 0; i < logicalRows.length; i++) {
            Row currentRowLogic = logicalRows[i];
            HBox rowBox = new HBox();
            rowBox.setSpacing(10);
            rowBox.setPadding(new Insets(2));
            rowBox.setAlignment(Pos.CENTER); 
            rowBox.setStyle(
                    "-fx-background-radius: 10; -fx-border-radius: 10;");
            rowBox.setUserData(currentRowLogic);

            for (int j = 0; j < 5; j++) {
                rowBox.getChildren().add(createPlaceholder(currentRowLogic));
            }

            rowBox.setOnMouseClicked(event -> {
                if (!game.isPlayerTurn() || game.hasPlayerActed()) {
                    return;
                }
                if (selectedCardView != null) {
                    Card cardToPlay = (Card) selectedCardView.getUserData();
                    Row targetRow = (Row) rowBox.getUserData();

                    Action.useCard(game.getActivePlayer(), cardToPlay, targetRow, game);
                    game.setPlayerHasActed(true);
                    updateUI();

                } else if (attackingCardView != null) {
                    Card cardToMove = (Card) attackingCardView.getUserData();
                    Row targetRow = (Row) rowBox.getUserData();

                    if (targetRow != cardToMove.getPosition() && cardToMove.getOwner() == game.getActivePlayer()) {
                        Action.moveCard(game.getActivePlayer(), cardToMove, targetRow);
                        game.setPlayerHasActed(true);
                        updateUI();
                    }
                }
            });

            grid.add(rowBox, 0, i);
        }
        return grid;
    }

    private void executeOpponentTurn() {
        opponentAI.executeTurn(this.game);
        updateUI();
    }

    private HBox createHandView(List<Card> cards) {
        HBox handBox = new HBox();
        handBox.setSpacing(10);
        handBox.setPadding(new Insets(10));
        handBox.setAlignment(Pos.CENTER);
        handBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-border-color: #5C4033;");

        for (Card card : cards) {
            StackPane cardView = createCardView(card, game.getPlayerA());
            handBox.getChildren().add(cardView);
        }

        return handBox;
    }

    private StackPane createCardView(Card card, Player owner) {
        StackPane cardView = new StackPane();
        cardView.setPrefSize(cardWidth, cardHeight);

        String artPath = card.getDesign();
        ImageView background = null;
        if (artPath != null && !artPath.isEmpty()) {
            background = createImageView("/" + artPath, cardWidth, cardHeight);
        }

        if (background != null) {
            cardView.getChildren().add(background);
        } else {
            createFallbackBackground(cardView);
        }

        String typeIconPath = "/assets/icon_" + card.getType().name().toLowerCase() + ".png";
        ImageView typeIcon = createImageView(typeIconPath, 30, 30);
        if (typeIcon != null) {
            StackPane.setAlignment(typeIcon, Pos.TOP_LEFT);
            typeIcon.setTranslateX(5);
            typeIcon.setTranslateY(5);
            cardView.getChildren().add(typeIcon);
        }

        Label hpLabel = new Label(String.valueOf(card.getHp()));
        hpLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: lightgreen; -fx-effect: dropshadow(gaussian, rgba(0,0,0,1), 6, 0, 0, 3);");
        StackPane.setAlignment(hpLabel, Pos.BOTTOM_LEFT);
        hpLabel.setTranslateX(10);
        hpLabel.setTranslateY(-5);

        Label damageLabel = new Label(String.valueOf(card.getDamage()));
        damageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: lightcoral; -fx-effect: dropshadow(gaussian, rgba(0,0,0,1), 6, 0, 0, 3);");
        StackPane.setAlignment(damageLabel, Pos.BOTTOM_RIGHT);
        damageLabel.setTranslateX(-10);
        damageLabel.setTranslateY(-5);

        Label nameLabel = new Label(card.getName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 2 5;");
        nameLabel.setVisible(false);
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(cardWidth - 10);
        StackPane.setAlignment(nameLabel, Pos.TOP_CENTER);
        nameLabel.setTranslateY(5);

        cardView.getChildren().addAll(nameLabel, hpLabel, damageLabel);
        cardView.setUserData(card);

        cardView.setOnMouseEntered(event -> nameLabel.setVisible(true));
        cardView.setOnMouseExited(event -> nameLabel.setVisible(false));


        cardView.setOnMouseClicked(event -> {
            if (!game.isPlayerTurn()) {
                return;
            }
            if (game.hasPlayerActed()) {
                attackingCardView = null;
                selectedCardView = null;
                return;
            }

            boolean isCardOnBoard = owner.getPlayedCards().contains(card);

            if (isCardOnBoard) {
                if (owner == game.getActivePlayer()) {
                    if (cardView == attackingCardView) {
                        clearRowHighlights();
                        cardView.setStyle("");
                        attackingCardView = null;
                    } else {
                        if (attackingCardView != null) {
                            attackingCardView.setStyle("");
                        }
                        highlightAttackRange(card);
                        cardView.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 8;");
                        attackingCardView = cardView;
                    }
                }
                else {
                    if (attackingCardView != null && !game.hasPlayerActed()) {
                        Card attackingCard = (Card) attackingCardView.getUserData();
                        Card targetCard = (Card) cardView.getUserData();

                        System.out.println(attackingCard.getName() + " ataca " + targetCard.getName() + "!");
                        Action.attackCard(game.getActivePlayer(), owner, attackingCard, targetCard);

                        game.setPlayerHasActed(true);
                        updateUI();
                    }
                }
            } else {
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
            }
        });

        return cardView;
    }

    private void createFallbackBackground(StackPane cardView) {
        Rectangle fallbackBg = new Rectangle(cardWidth, cardHeight, Color.LIGHTGREY);
        fallbackBg.setStroke(Color.BLACK);
        fallbackBg.setArcWidth(10);
        fallbackBg.setArcHeight(10);
        cardView.getChildren().add(fallbackBg);
    }
    private ImageView createImageView(String path, double width, double height) {
        try (var stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.err.println("Imagem não encontrada: " + path);
                return null;
            }
            Image image = new Image(stream);
            ImageView imageView = new ImageView(image);
            if (width > 0) imageView.setFitWidth(width);
            if (height > 0) imageView.setFitHeight(height);
            return imageView;
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + path);
            return null;
        }
    }

    private void highlightAttackRange(Card attacker) {
        clearRowHighlights();
        int attackerRowValue = attacker.getPosition().getValue();
        int range = attacker.getType().getRange();

        for (Node node : battlefieldGrid.getChildren()) {
            if (node instanceof HBox) {
                HBox rowBox = (HBox) node;
                Row rowLogic = (Row) rowBox.getUserData();
                int distance = Math.abs(rowLogic.getValue() - attackerRowValue);

                if (distance > 0 && distance <= range) {
                    rowBox.setStyle("-fx-background-color: rgba(255, 255, 0, 0.2); -fx-border-color: yellow; -fx-border-width: 1; -fx-background-radius: 10; -fx-border-radius: 10;");
                }
            }
        }
    }

    private void clearRowHighlights() {
        for (Node node : battlefieldGrid.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
            }
        }
    }
    private Node createPlaceholder(Row row) {
        Rectangle placeholder = new Rectangle(cardWidth, cardHeight, Color.TRANSPARENT);

        if (row.getSide() == 'B') {
            placeholder.setStroke(Color.web("rgba(255, 50, 50, 0.4)"));
        } else {
            placeholder.setStroke(Color.web("rgba(255, 255, 255, 0.3)"));
        }

        placeholder.getStrokeDashArray().addAll(10d, 5d);
        placeholder.setArcWidth(10);
        placeholder.setArcHeight(10);
        return placeholder;
    }

    private void showGameOverAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Fim de Jogo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        endTurnButton.setDisable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}