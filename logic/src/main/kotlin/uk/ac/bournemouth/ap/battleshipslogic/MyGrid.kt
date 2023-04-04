package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random

class MyGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: MyOpponent
) : BattleshipGrid{

    val data = MutableMatrix<GuessCell>(columns, rows) {col, row -> GuessCell.UNSET }

    override val shipsSunk = BooleanArray(opponent.shipTypes.size)

    private val gridChangeListeners = mutableListOf<BattleshipGrid.BattleshipGridListener>()


    override fun get(column: Int, row: Int): GuessCell {
        return data[column, row]
    }

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

    //Guess a cell by randomly selecting a column and row int.
    fun randomGuess(columns: Int, rows: Int, random: Random): Coordinate {
        val guessColumn = random.nextInt(0, columns)
        val guessRow = random.nextInt(0, rows)
        return Coordinate(guessColumn, guessRow)
    }

    //Check that a selected cell has not been shot at before.
    fun isGuessValid(guessCell:Coordinate): Boolean{
        return data[guessCell.x, guessCell.y] == GuessCell.UNSET
    }

    //Adds to a ships hits value returns true if a ship as been sunk
    private fun checkIfSunk(ship: MyShip): Boolean{
        ship.hits++
        return ship.isSunk
    }

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        if(listener !in gridChangeListeners){
            gridChangeListeners.add(listener)
        }
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        gridChangeListeners.remove(listener)
    }

    fun onGridChanged(column: Int, row: Int){
        for(listener in gridChangeListeners){
            listener.onGridChanged(this, column, row)
        }
    }
}