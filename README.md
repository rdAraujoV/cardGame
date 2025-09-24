# Card Game Project

This project is a turn-based card game, built entirely from scratch in Java, inspired by the card game Gwent from *The Witcher 3: Wild Hunt*. It is currently in a playable pre-alpha state.

The game showcases a comprehensive approach to software development, from core game logic and object-oriented design to a robust testing suite with JUnit. The graphical user interface is built with JavaFX, providing a complete and interactive player experience.

*(Consider adding a screenshot of your game here!)*

## Key Features

*   **Core Gameplay Mechanics**: Implements fundamental actions like playing, moving, and attacking with cards on a multi-row battlefield.
*   **Turn-Based System**: A complete turn-based loop allows for gameplay against a simple AI opponent.
*   **Dynamic Card System**: Cards are loaded from an external `card.json` file, allowing for easy expansion and modification.
*   **Object-Oriented Design**: Cleanly structured classes for `Player`, `Card`, `Deck`, `Row`, and `Battlefield` to model the game's logic.
*   **Unit Tested**: Core game actions are validated with a suite of JUnit 5 tests to ensure reliability.
*   **Graphical User Interface**: A functional UI built with JavaFX to visualize the game state, including player hands, the battlefield, and life points.

## Technology Stack

*   **Language**: Java 17
*   **Build Automation**: Maven
*   **GUI Framework**: JavaFX
*   **Testing**: JUnit 5
*   **JSON Parsing**: Google Gson

## How to Play

The goal is to reduce your opponent's life to zero.

1.  **Play a Card**: Click a card in your hand, then click an empty spot on one of your rows to play it.
2.  **Move a Card**: Click one of your cards already on the battlefield, then click a different row on your side to move it.
3.  **Attack a Card**: Click one of your cards on the battlefield, then click an enemy card to attack it.
4.  **Attack the Player**: If one of your cards is on the opponent's back row, you can click your card and then click the opponent's player info box to deal 1 damage directly.
5.  **End Your Turn**: After performing one action (play, move, or attack), click the "Finalizar Turno" button to pass control to the AI.

The game ends when one player's life reaches 0.

## How to Run

As this is a Maven project with the JavaFX plugin configured, you can run it directly from the command line.

1.  Ensure you have Java 17 (or newer) and Maven installed.
2.  Navigate to the project's root directory (`c:\cardgame`).
3.  Run the following command:

    ```sh
    mvn javafx:run
    ```