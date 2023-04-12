package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random
/**
 *A class that represents the Battleship game as a whole.
 *
 * @author David Whalley
 *
 * @param columns The maximum amount of grid columns.
 * @param rows The maximum amount of grid rows.
 */
class MyBattleShipGame(val columns: Int, val rows: Int){

    /**
     * A list of default ship types.
     * Each ship represented by an integer depicting the length.
    */
    private val shipTypes = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )

    /**
     * Constructs two players of type MyOpponent.
     */
    val redPlayer = MyOpponent(columns, rows, shipTypes, Random)

    val bluePlayer = MyOpponent(columns, rows, shipTypes, Random)

    /**
     * Constructs two player grids of type MyGrid each with a different opponent.
     */
    var redGrid = MyGrid(columns, rows, bluePlayer)

    var blueGrid = MyGrid(columns, rows, redPlayer)

    /**
     * Selects a random integer 1 or 2.
     * Used to randomly decide which players turn is first.
     */
    var turn:Int = Random.nextInt(2)+1

    /**
     * Main function for a single player game.
     * Represents the human player taking a turn followed by the computer player taking a turn.
     *
     * @param column of a human guessed cell.
     * @param row of a human guessed cell.
     */
    fun playerGame(column: Int, row: Int){
        if(column < columns && row < rows){
            if(blueGrid.isGuessValid(Coordinate(column, row))) {
                blueGrid.shootAt(column, row)
                blueGrid.onGridChanged(column, row)
                playTurn(columns, rows, redGrid)
                redGrid.onGridChanged(column, row)
            }
        }

    }

    /**
     * Represents the computer playing a turn.
     *
     * @param columns The maximum amount of grid columns.
     * @param rows The maximum amount of grid rows.
     * @param grid The opponents grid the current player is shooting at.
     */
    private fun playTurn(columns: Int, rows: Int, grid: MyGrid){
        var guessCell: Coordinate
        grid.opponent.tactics.clear()
        grid.isAnyHits()
        do {
            guessCell = if(grid.opponent.tactics.isNotEmpty()){
                grid.tacticalGuess(grid.opponent.tactics, Random)
            } else {
                grid.randomGuess(columns, rows, Random)
            }
        }
        while(!grid.isGuessValid(guessCell))
        grid.shootAt(guessCell.x, guessCell.y)
        turn = (turn % 2)+1
    }

    /**
     * Main function for a computer vs computer player game.
     * Represents two computer players taking turns.
     */
    fun playGame(){
        if(turn == 1){
            playTurn(columns, rows, blueGrid)
        }
        else{
            playTurn(columns, rows, redGrid)
        }
    }

    /**
     * Main function for a two player game.
     * Represents a human player taking a turn then passing to the next human player.
     *
     * @param column of a human guessed cell.
     * @param row of a human guessed cell.
     * @param grid the grid the active player is shooting at.
     */
    fun twoPlayerGame(column: Int, row: Int, grid: MyGrid){
        if(column < columns && row < rows){
            if(grid.isGuessValid(Coordinate(column, row))) {
                grid.shootAt(column, row)
                grid.onGridChanged(column, row)
                turn = (turn % 2)+1
            }
        }
    }

    /**
     * An interface for when a game has finished.
     *
     * @author David Whalley
     */
    interface OnGameFinishedListener {
        /**
         * When a game has finished this method is called.
         */
        fun onGameFinished()
    }
}