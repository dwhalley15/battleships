package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random
/**
 *A class that represents a players grid.
 *
 * @author David Whalley
 *
 * @param columns The maximum amount of grid columns.
 * @param rows The maximum amount of grid rows.
 * @param opponent The opponent player guessing/shooting at this grid, of type MyOpponent.
 */
class MyGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: MyOpponent
) : BattleshipGrid{

    /**
     * Creates a grid with the size of columns and rows.
     * Each cell is of type GuessCell.
     * Sets the state of each cell to UNSET.
     */
    val data = MutableMatrix<GuessCell>(columns, rows) { _, _ -> GuessCell.UNSET }

    /**
     * Creates a list the size of the amount of ships.
     * Sets each value to false.
     * Represents the amount of ships that have and have not been sunk.
     * Used to confirm the end of the game.
     */
    override val shipsSunk = BooleanArray(opponent.shipTypes.size)

    /**
     * Creates a list of listeners.
     * Used to update the UI when the grid has changed.
     */
    private val gridChangeListeners = mutableListOf<BattleshipGrid.BattleshipGridListener>()

    /**
     * Retreives a cell from the grid based on parameters.
     *
     * @param column of a grid cell.
     * @param row of a grid cell.
     *
     * @return The cell located on the grid at the desired location.
     */
    override fun get(column: Int, row: Int): GuessCell {
        return data[column, row]
    }

    /**
     * Main function for any game.
     * Represents a player shooting at an opponents grid.
     *
     * @param column of a shot.
     * @param row of a shot.
     *
     * @return The result of the shot of type GuessResult MISS, HIT or SUNK.
     */
    override fun shootAt(column: Int, row: Int): GuessResult {
        return if(opponent.shipAt(column, row) != null){
            val ship = opponent.shipAt(column, row)!!.ship
            val index = opponent.shipAt(column, row)!!.index
            data[column, row] = GuessCell.HIT(index)
            if(checkIfSunk(ship)){
                for(col in ship.left..ship.right){
                    for(r in ship.top..ship.bottom){
                        data[col, r] = GuessCell.SUNK(index)
                    }
                }
                shipsSunk[index] = true
                GuessResult.SUNK(index)
            }
            GuessResult.HIT(index)
        } else{
            data[column, row] = GuessCell.MISS
            GuessResult.MISS
        }
    }

    /**
     * A function for a computer player turn.
     * Represents a player guessing a random location on the grid.
     *
     * @param columns The maximum amount of grid columns.
     * @param rows The maximum amount of grid rows.
     * @param random A random abstract class.
     *
     * @return Type Coordinate of a grid location.
     */
    fun randomGuess(columns: Int, rows: Int, random: Random): Coordinate {
        val guessColumn = random.nextInt(0, columns)
        val guessRow = random.nextInt(0, rows)
        return Coordinate(guessColumn, guessRow)
    }

    /**
     * A function for a computer player turn.
     * Represents a player guessing a location based on previous hits.
     *
     * @param tactics A list of type Coordinate to guess from.
     * @param random A random abstract class.
     *
     * @return Type Coordinate of a grid location.
     */
    fun tacticalGuess(tactics: MutableList<Coordinate>, random: Random): Coordinate {
        val randomIndex = random.nextInt(tactics.size)
        return tactics[randomIndex]
    }

    /**
     * A function to check a given coordinate is on the grid.
     *
     * @param guessCell A candidate guess of type Coordinate.
     *
     * @return True or false depending on if the given guessCell Coordinate exists on the grid.
     */
    private fun isOnGrid(guessCell:Coordinate): Boolean{
        return (guessCell.x < columns && guessCell.y < rows) && (guessCell.x >= 0 && guessCell.y >= 0)
    }

    /**
     * A function to check a given coordinate has not already been guessed.
     *
     * @param guessCell A candidate guess of type Coordinate.
     *
     * @return True or false depending on if the given guessCell on the grid of type GuessCell  is UNSET.
     */
    fun isGuessValid(guessCell:Coordinate): Boolean{
        return data[guessCell.x, guessCell.y] == GuessCell.UNSET
    }

    /**
     * A function to check if a given ship has been sunk.
     *
     * @param ship A give ship of type MyShip.
     *
     * @return True or false depending on if the given ship has been sunk.
     */
    private fun checkIfSunk(ship: MyShip): Boolean{
        ship.hits++
        return ship.isSunk
    }

    /**
     * A function to check if the opponent (to this grid) has already scored any hits.
     * Used to generate tactical guesses based on existing hits.
     */
    fun isAnyHits(){
        opponent.hits.clear()
        opponent.tactics.clear()
        for(col in 0 until columns){
            for(row in 0 until rows) {
                if (data[col, row] == GuessCell.HIT(0) || data[col, row] == GuessCell.HIT(1) || data[col, row] == GuessCell.HIT(2) || data[col, row] == GuessCell.HIT(3) || data[col, row] == GuessCell.HIT(4)) {
                    val hit = Coordinate(col, row)
                    opponent.hits.add(hit)
                }
            }
        }
        if(opponent.hits.isNotEmpty()){
            huntTactics(Random)
        }
    }

    /**
     * A function to generate tactical guesses.
     */
    private fun huntTactics(random: Random){
        do{
            val index = random.nextInt(opponent.hits.size)
            val potentialTactics = mutableListOf(Coordinate(opponent.hits[index].x+1, opponent.hits[index].y), Coordinate(opponent.hits[index].x-1,opponent.hits[index].y), Coordinate( opponent.hits[index].x, opponent.hits[index].y+1), Coordinate(opponent.hits[index].x, opponent.hits[index].y-1))
            val newTactics = mutableListOf<Coordinate>()
            for (tactic in potentialTactics){
                if(isOnGrid(tactic) && isGuessValid(tactic)){
                    newTactics.add(tactic)
                }
            }
            opponent.tactics.clear()
            opponent.tactics.addAll(newTactics)
        }
            while(opponent.tactics.isEmpty())
    }

    /**
     * Register a listener for when the grid changes.
     *
     * @param listener The listener to register.
     */
    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        if(listener !in gridChangeListeners){
            gridChangeListeners.add(listener)
        }
    }

    /**
     * Unregister a listener so that it no longer receives notifications of grid changes.
     *
     * @param listener The listener to unregister.
     */
    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        gridChangeListeners.remove(listener)
    }

    /**
     * This function is called when this grid has changed.
     *
     * @param column The column that has changed.
     * @param row The row that has changed.
     */
    fun onGridChanged(column: Int, row: Int){
        for(listener in gridChangeListeners){
            listener.onGridChanged(this, column, row)
        }
    }
}