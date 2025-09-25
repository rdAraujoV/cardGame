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
    // Constantes de Layout
    private static final double CARD_WIDTH = 80, CARD_HEIGHT = 112, PADDING = 10;
    
    // Logic
    private GameLogic game; 

    // Componentes da UI
    private StackPane selectedCardView = null;
    private StackPane attackingCardView = null;
    private HBox playerAHandBox, playerBHandBox;
    private GridPane battlefieldGrid;
    private Circle playerAIndicator, playerBIndicator;
    private Label playerALifeLabel, playerBLifeLabel;
    private Button endTurnButton;

    @Override
    public void start(Stage primaryStage) {
        this.game = new GameLogic();
        primaryStage.setTitle("Card Game");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1280, 720);

        // Montagem da UI
        this.battlefieldGrid = createBattlefieldView(game.getBattlefield());
        root.setCenter(this.battlefieldGrid);

        // Painel do Jogador A (Inferior)
        this.playerALifeLabel = new Label();
        VBox playerAView = createPlayerView(game.getPlayerA(), this.playerALifeLabel);
        this.playerAHandBox = createHandView(game.getPlayerA().getHand().getCards());
        this.playerAIndicator = new Circle(15, Color.GOLD);
        this.playerAIndicator.setStroke(Color.BLACK);
        HBox bottomContainer = new HBox(30, playerAView, this.playerAHandBox, this.playerAIndicator);
        bottomContainer.setPadding(new Insets(PADDING));
        bottomContainer.setAlignment(Pos.CENTER);
        root.setBottom(bottomContainer);

        // Painel do Jogador B (Superior)
        this.playerBLifeLabel = new Label();
        VBox playerBView = createPlayerView(game.getPlayerB(), this.playerBLifeLabel);
        this.playerBHandBox = createOpponentHandView(game.getPlayerB().getHand().size()); // CORRIGIDO
        this.playerBIndicator = new Circle(15, Color.GOLD);
        this.playerBIndicator.setStroke(Color.BLACK);
        HBox topContainer = new HBox(30, playerBView, this.playerBHandBox, this.playerBIndicator);
        topContainer.setPadding(new Insets(PADDING));
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

        // Painel de Ações (Direita)
        this.endTurnButton = new Button("Finalizar Turno");
        this.endTurnButton.setPrefSize(120, 60);
        this.endTurnButton.setOnAction(event -> endTurn());
        VBox rightSideBar = new VBox(this.endTurnButton);
        rightSideBar.setAlignment(Pos.CENTER);
        rightSideBar.setPadding(new Insets(20));
        root.setRight(rightSideBar);

        
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

    private void updateUI() {
        // Mãos
        playerAHandBox.getChildren().clear();
        for (Card card : game.getPlayerA().getHand().getCards()) {
            playerAHandBox.getChildren().add(createCardView(card, game.getPlayerA()));
        }
        playerBHandBox.getChildren().clear();
        for (int i = 0; i < game.getPlayerB().getHand().size(); i++) {
            playerBHandBox.getChildren().add(createCardBackView());
        }

        // Tabuleiro
        for (javafx.scene.Node node : battlefieldGrid.getChildren()) {
            if (node instanceof HBox) {
                HBox rowBox = (HBox) node;
                Row rowLogic = (Row) rowBox.getUserData();
                rowBox.getChildren().clear();
                if (rowLogic.getCards().isEmpty()) {
                    for (int i = 0; i < 5; i++) rowBox.getChildren().add(createPlaceholder());
                } else {
                    for (Card card : rowLogic.getCards()) rowBox.getChildren().add(createCardView(card, card.getOwner()));
                }
            }
        }

        // Vidas e Indicadores
        playerALifeLabel.setText("Vida: " + game.getPlayerA().getLife());
        playerBLifeLabel.setText("Vida: " + game.getPlayerB().getLife());
        playerAIndicator.setVisible(game.isPlayerTurn());
        playerBIndicator.setVisible(!game.isPlayerTurn());

        // Limpa seleção
        if (selectedCardView != null) selectedCardView.setTranslateY(0);
        if (attackingCardView != null) attackingCardView.setStyle("-fx-border-color: black;");
        selectedCardView = null;
        attackingCardView = null;

        // Verifica vitória
        Player winner = game.getWinner();
        if (winner != null) {
            showGameOverAlert("Vitória de " + winner.getName() + "!");
        }
    }

    private VBox createPlayerView(Player player, Label lifeLabel) {
        VBox playerBox = new VBox(10);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(20));
        playerBox.setStyle("-fx-border-color: black;");

        Label nameLabel = new Label(player.getName());
        lifeLabel.setText("Vida: " + player.getLife());

        playerBox.getChildren().addAll(nameLabel, lifeLabel);
        return playerBox;
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

    private HBox createHandView(List<Card> cards) {
        HBox handBox = new HBox();
        handBox.setSpacing(10);
        handBox.setPadding(new Insets(10));
        handBox.setAlignment(Pos.CENTER);
        handBox.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");

        for (Card card : cards) {
            StackPane cardView = createCardView(card, game.getPlayerA());
            handBox.getChildren().add(cardView);
        }

        return handBox;
    }

    private HBox createOpponentHandView(int cardCount) {
        HBox handBox = new HBox();
        handBox.setSpacing(10);
        handBox.setPadding(new Insets(10));
        handBox.setAlignment(Pos.CENTER);
        handBox.setStyle("-fx-background-color: lightcoral; -fx-border-color: black;");

        for (int i = 0; i < cardCount; i++) {
            Rectangle cardBack = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.DARKSLATEBLUE);
            cardBack.setStroke(Color.BLACK);
            handBox.getChildren().add(cardBack);
        }
        return handBox;
    }

    private StackPane createCardView(Card card, Player owner) {
        StackPane cardView = new StackPane();
        // --- Configuração Visual (como antes) ---
        cardView.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        Rectangle background = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.LIGHTYELLOW);
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

        // --- LÓGICA DE CLIQUE ---
        cardView.setOnMouseClicked(event -> {
            // 1. Bloqueia qualquer interação se não for o turno do jogador A
            if (!game.isPlayerTurn()) {
                return;
            }

            // 2. Verifica se a carta está no tabuleiro ou na mão
            boolean isCardOnBoard = owner.getPlayedCards().contains(card);

            // SE A CARTA ESTÁ NO TABULEIRO
            if (isCardOnBoard) {
                // Se pertence ao jogador ativo (é um ATACANTE em potencial)
                if (owner == game.getActivePlayer()) {
                    if (cardView == attackingCardView) {
                        cardView.setStyle("-fx-border-color: black; -fx-background-color: white;"); // Estilo normal
                        attackingCardView = null;
                    } else {
                        if (attackingCardView != null) {
                            attackingCardView.setStyle("-fx-border-color: black; -fx-background-color: white;");
                        }
                        cardView.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-background-color: white;");
                        attackingCardView = cardView;
                    }
                }
                // Se pertence ao oponente (é um ALVO em potencial)
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
                // SE A CARTA ESTÁ NA MÃO
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

    // logica de IA
    private void executeOpponentTurn() {
        System.out.println("Oponente (Player B) está pensando...");

        if (game.getPlayerB().getHand().size() > 0) {
            Card cardToPlay = game.getPlayerB().getHand().getCards().get(0);

            Row targetRow = findEmptyRow(game.getBattlefield().getFrontRowB());
            if (targetRow == null)
                targetRow = findEmptyRow(game.getBattlefield().getMidRowB());
            if (targetRow == null)
                targetRow = findEmptyRow(game.getBattlefield().getBackRowB());
            if (targetRow == null)
                targetRow = findEmptyRow(game.getBattlefield().getFrontRowA());
            if (targetRow == null)
                targetRow = findEmptyRow(game.getBattlefield().getMidRowA());
            if (targetRow == null)
                targetRow = findEmptyRow(game.getBattlefield().getBackRowA());
            if (targetRow != null) {
                System.out.println("Oponente joga: " + cardToPlay.getName());
                Action.useCard(game.getPlayerB(), cardToPlay, targetRow, game);
            }
        }

        System.out.println("Oponente finalizou o turno.");
        endTurn();
    }

    private Row findEmptyRow(Row row) {
        if (row.getCards().size() < 5) {
            return row;
        }
        return null;
    }

    private Node createPlaceholder() {
        Rectangle placeholder = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.LIGHTGRAY);
        placeholder.setStroke(Color.BLACK);
        return placeholder;
    }

    private Node createCardBackView() {
        Rectangle cardBack = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.DARKSLATEBLUE);
        cardBack.setStroke(Color.BLACK);
        cardBack.setArcWidth(10);
        cardBack.setArcHeight(10);
        return cardBack;
    }


    private void showGameOverAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Fim de Jogo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        // Desativa o botão para impedir mais ações
        endTurnButton.setDisable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}