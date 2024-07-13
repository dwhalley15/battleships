Battleships version 1.0 12/04/2023
==================================

A simple battleships game. Written in Kotlin for Andriod. The logic and UI are seperated so a different UI can be utlised.

Location
========

https://github.com/dwhalley15/battleships

Requirements
============

Android studio/emulator
gradle
SDK 31 or higher
JVM 18 or higher

Extensions
==========

MyBattleShipGame - A class representing the game as a whole. Creates both players and both grids. Keeps track of player turns. Provides the interface for the
                   OnGameFinished Listener. Holds implementation for all game modes. This is the only class that the UI needs to initialise.

Two versions of opponent - MyOpponent takes in a list of ship types/size and random, then generates a list of valid randomly placed ships.
                           MySecondOpponent takes in a list of already placed ships and validates thier placements.

Two versions of grid - MyGrid holds implementation for AI/computer controlled players and validates the shots before they are made.
                       MySecondGrid validates shots when they are made.

Improved computer/AI player - Generates tactical guesses based on previously scored hits. When no hits are present returns to guessing a random valid cell.

Game Modes 
==========

All modes are currently accessible in the UI.

The top grid holds red players ships which blue player shoots at.
The bottom grid holds blue players ships which red player shoots at.

Player vs computer - blue player = computer, red player = human, red player always goes first.

Player vs player - both players are human first turn randomly selected.

Computer vs computer both players are computer first turn randomly selected.
