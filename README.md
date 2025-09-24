# Card Game Project

This project is a turn-based card game, built entirely from scratch using Java. It showcases a comprehensive approach to software development, from core game logic and object-oriented design to a robust testing suite developed with JUnit. The graphical user interface is currently being implemented with JavaFX to provide an interactive player experience.

## Key Features

*   **Core Gameplay Mechanics**: Implements fundamental actions like playing, moving, and attacking with cards on a multi-row battlefield.
*   **Dynamic Card System**: Cards are loaded from an external `card.json` file, allowing for easy expansion and modification.
*   **Object-Oriented Design**: Cleanly structured classes for `Player`, `Card`, `Deck`, `Row`, and `Battlefield` to model the game's logic.
*   **Unit Tested**: Core game actions are validated with a suite of JUnit 5 tests to ensure reliability.
*   **Graphical User Interface**: A developing UI built with JavaFX to visualize the game state, including player hands and the battlefield.

## Technology Stack

*   **Language**: Java 17
*   **Build Automation**: Maven
*   **GUI Framework**: JavaFX
*   **Testing**: JUnit 5
*   **JSON Parsing**: Google Gson

## How to Run

As this is a Maven project with the JavaFX plugin configured, you can run it directly from the command line.

1.  Ensure you have Java 17 (or newer) and Maven installed.
2.  Navigate to the project's root directory (`c:\cardgame`).
3.  Run the following command:

    ```sh
    mvn javafx:run
    ```