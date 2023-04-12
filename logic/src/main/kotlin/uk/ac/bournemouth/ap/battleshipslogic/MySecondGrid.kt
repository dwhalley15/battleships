package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
/**
 *A class that represents a players grid.
 *This version takes in an opponent of type MySecondOpponent and validates shots on shootAt being run.
 *
 * @author David Whalley
 *
 * @param columns The maximum amount of grid columns.
 * @param rows The maximum amount of grid rows.
 * @param opponent The opponent player guessing/shooting at this grid, of type MySecondOpponent.
 */
class MySecondGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: MySecondOpponent
) : BattleshipGrid {

    /**
     * Creates a grid with the size of columns and rows.
     * Each cell is of type GuessCell.
     * Sets the state of each cell to UNSET.
     */
    private val data = MutableMatrix<GuessCell>(columns, rows) { _, _ -> GuessCell.UNSET }

    /**
     * Creates a list of listeners.
     * Used to update the UI when the grid has changed.
     */
    private val gridChangeListeners = mutableListOf<BattleshipGrid.BattleshipGridListener>()

    /**
     * Creates a list the size of the amount of ships.
     * Sets each value to false.
     * Represents the amount of ships that have and have not been sunk.
     * Used to confirm the end of the game.
     */
    override val shipsSunk = BooleanArray(opponent.ships.size)

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
        require(isOnGrid(Coordinate(column, row)))
        require(data[column, row] == GuessCell.UNSET)
        if (opponent.shipAt(column, row) != null) {
            val ship = opponent.shipAt(column, row)!!.ship
            val index = opponent.shipAt(column, row)!!.index
            data[column, row] = GuessCell.HIT(index)
            if (checkIfSunk(ship, index)) {
                for (col in ship.left..ship.right) {
                    for (r in ship.top..ship.bottom) {
                        data[col, r] = GuessCell.SUNK(index)
                    }
                }
                shipsSunk[index] = true
                onGridChanged(column, row)
                return GuessResult.SUNK(index)
            }
            onGridChanged(column, row)
            return GuessResult.HIT(index)
        }
        else {
            data[column, row] = GuessCell.MISS
            onGridChanged(column, row)
            return GuessResult.MISS
        }
    }

    /**
     * A function to check if a given ship has been sunk.
     *
     * @param ship A given ship of type MyShip.
     * @param index Index of the given ship
     *
     * @return True or false depending on if the given ship has been sunk.
     */
    private fun checkIfSunk(ship: MyShip, index: Int): Boolean{
        var hits = 0
        for(col in 0 until columns){
            for(row in 0 until rows){
                if(data[col, row] == GuessCell.HIT(index)){
                    hits++
                }
            }
        }
        if(hits == ship.size){
            return true
        }
        return false
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
    private fun onGridChanged(column: Int, row: Int){
        for(listener in gridChangeListeners){
            listener.onGridChanged(this, column, row)
        }
    }

    /**
     * This function checks that a given coordinate is on the grid.
     *
     * @param guessCell The given coordinate.
     *
     * @return True or false depending if the shot is on the grid.
     */
    private fun isOnGrid(guessCell:Coordinate): Boolean{
        return (guessCell.x < columns && guessCell.y < rows) && (guessCell.x >= 0 && guessCell.y >= 0)
    }

}