package com.cardgame;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    // consts
    private static final double CARD_WIDTH = 80;
    private static final double CARD_HEIGHT = 100;
    private static final double PADDING = 5;
    // logic
    private Battlefield battlefield;
    private Player playerA;
    private Player playerB;

    // UI
    private StackPane selectedCardView = null;
    private StackPane attackingCardView = null;
    private HBox playerAHandBox;
    private HBox playerBHandBox;
    private GridPane battlefieldGrid;
    private Circle playerAIndicator;
    private Circle playerBIndicator;
    private Label playerALifeLabel;
    private Label playerBLifeLabel;
    private Button endTurnButton;

    // outros
    private Player activePlayer;
    private boolean isPlayerTurn = true;
    private boolean playerHasActed = false;

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

        this.activePlayer = this.playerA;
        System.out.println("O turno começa com: " + this.activePlayer.getName());
    }

    @Override
    public void start(Stage primaryStage) {
        // --- 1. Inicialização ---
        initializeGameLogic();
        primaryStage.setTitle("Card Game");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1280, 720);

        // --- 2. Painel Central: Campo de Batalha ---
        this.battlefieldGrid = createBattlefieldView(this.battlefield);
        root.setCenter(this.battlefieldGrid);

        // --- 3. Painel Inferior: Jogador A ---
        this.playerALifeLabel = new Label();
        VBox playerAView = createPlayerView(playerA, this.playerALifeLabel);
        this.playerAHandBox = createHandView(playerA.getHand().getCards());
        this.playerAIndicator = new Circle(15, Color.GOLD);
        this.playerAIndicator.setStroke(Color.BLACK);

        HBox bottomContainer = new HBox(30, playerAView, this.playerAHandBox, this.playerAIndicator);
        bottomContainer.setPadding(new Insets(10));
        bottomContainer.setAlignment(Pos.CENTER);
        root.setBottom(bottomContainer);

        // --- 4. Painel Superior: Jogador B ---
        this.playerBLifeLabel = new Label();
        VBox playerBView = createPlayerView(playerB, this.playerBLifeLabel);
        this.playerBHandBox = createOpponentHandView(playerB.getHand().size());
        this.playerBIndicator = new Circle(15, Color.GOLD);
        this.playerBIndicator.setStroke(Color.BLACK);

        HBox topContainer = new HBox(30, playerBView, this.playerBHandBox, this.playerBIndicator);
        topContainer.setPadding(new Insets(10));
        topContainer.setAlignment(Pos.CENTER);
        root.setTop(topContainer);

        playerBView.setOnMouseClicked(event -> {
            // A ação só acontece se for o turno do jogador, ele não tiver agido, e uma
            // carta atacante estiver selecionada
            if (isPlayerTurn && !playerHasActed && attackingCardView != null) {

                // 1. Pega a carta atacante
                Card attackingCard = (Card) attackingCardView.getUserData();

                // 2. Chama a lógica do jogo
                Action.attackPlayer(playerA, playerB, attackingCard, this.battlefield);

                // 3. O jogador gasta sua ação
                playerHasActed = true;

                // 4. Atualiza a tela para mostrar a nova vida do oponente
                updateUI();
            }
        });

        // --- 5. Painel Direito: Ações ---
        Button endTurnButton = new Button("Finalizar Turno");
        endTurnButton.setPrefSize(120, 60);
        endTurnButton.setOnAction(event -> endTurn());

        VBox rightSideBar = new VBox(endTurnButton);
        rightSideBar.setAlignment(Pos.CENTER);
        rightSideBar.setPadding(new Insets(20));
        root.setRight(rightSideBar);

        // --- 6. Montagem Final ---
        primaryStage.setScene(scene);
        primaryStage.show();
        updateUI(); // Chama no final para garantir o estado
    }

    private VBox createPlayerView(Player player, Label lifeLabel) {
        VBox playerBox = new VBox(10); // Espaçamento 10
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(20));
        playerBox.setStyle("-fx-border-color: black;");

        Label nameLabel = new Label(player.getName());
        // Inicializa o label de vida com o valor atual
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
                if (!isPlayerTurn || playerHasActed) {
                    return;
                }
                if (selectedCardView != null) {
                    Card cardToPlay = (Card) selectedCardView.getUserData();
                    Row targetRow = (Row) rowBox.getUserData();

                    Action.useCard(playerA, cardToPlay, targetRow);
                    playerHasActed = true;
                    updateUI();

                } else if (attackingCardView != null) {
                    Card cardToMove = (Card) attackingCardView.getUserData();
                    Row targetRow = (Row) rowBox.getUserData();

                    if (targetRow != cardToMove.getPosition()) {
                        Action.moveCard(playerA, cardToMove, targetRow);
                        playerHasActed = true;
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
            StackPane cardView = createCardView(card, playerA);
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
            if (!isPlayerTurn) {
                return;
            }

            // 2. Verifica se a carta está no tabuleiro ou na mão
            boolean isCardOnBoard = owner.getPlayedCards().contains(card);

            // SE A CARTA ESTÁ NO TABULEIRO
            if (isCardOnBoard) {
                // Se pertence ao jogador ativo (é um ATACANTE em potencial)
                if (owner == activePlayer) {
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
                    if (attackingCardView != null && playerHasActed == false) {
                        Card attackingCard = (Card) attackingCardView.getUserData();
                        Card targetCard = (Card) cardView.getUserData();

                        System.out.println(attackingCard.getName() + " ataca " + targetCard.getName() + "!");
                        Action.attackCard(activePlayer, owner, attackingCard, targetCard);

                        playerHasActed = true;
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

        if (playerB.getHand().size() > 0) {
            Card cardToPlay = playerB.getHand().getCards().get(0);

            Row targetRow = findEmptyRow(this.battlefield.getFrontRowB());
            if (targetRow == null)
                targetRow = findEmptyRow(this.battlefield.getMidRowB());
            if (targetRow == null)
                targetRow = findEmptyRow(this.battlefield.getBackRowB());
            if (targetRow == null)
                targetRow = findEmptyRow(this.battlefield.getFrontRowA());
            if (targetRow == null)
                targetRow = findEmptyRow(this.battlefield.getMidRowA());
            if (targetRow == null)
                targetRow = findEmptyRow(this.battlefield.getBackRowA());

            if (targetRow != null) {
                System.out.println("Oponente joga: " + cardToPlay.getName());
                Action.useCard(playerB, cardToPlay, targetRow);
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

    private void updateUI() {
        playerAHandBox.getChildren().clear();
        for (Card card : playerA.getHand().getCards()) {
            playerAHandBox.getChildren().add(createCardView(card, playerA));
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
                        rowBox.getChildren().add(createCardView(card, card.getOwner()));
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

        playerALifeLabel.setText("Vida: " + playerA.getLife());
        playerBLifeLabel.setText("Vida: " + playerB.getLife());

        if (activePlayer == playerA) {
            playerAIndicator.setVisible(true);
            playerBIndicator.setVisible(false);
        } else {
            playerAIndicator.setVisible(false);
            playerBIndicator.setVisible(true);
        }

        if (selectedCardView != null) {
            selectedCardView.setTranslateY(0);
            selectedCardView = null;
        }
    }

    private void endTurn() {
        if (playerB.getLife() <= 0) {
            showGameOverAlert("Vitória do Jogador A!");
            return; // Encerra o método aqui
        }
        if (playerA.getLife() <= 0) {
            showGameOverAlert("Vitória do Jogador B!");
            return; // Encerra o método aqui
        }
        if (activePlayer == playerA) {
            activePlayer = playerB;
        } else {
            activePlayer = playerA;
            playerHasActed = false;
        }
        isPlayerTurn = (activePlayer == playerA);

        updateUI();

        // logica de IA
        if (!isPlayerTurn) {
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(1.5));
            pause.setOnFinished(event -> executeOpponentTurn());
            pause.play();
        }
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