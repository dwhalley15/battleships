package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

class MySecondGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: MySecondOpponent
) : BattleshipGrid {

    private val data = MutableMatrix<GuessCell>(columns, rows) { _, _ -> GuessCell.UNSET }

    private val gridChangeListeners = mutableListOf<BattleshipGrid.BattleshipGridListener>()

    override val shipsSunk = BooleanArray(opponent.ships.size)

    override fun get(column: Int, row: Int): GuessCell {
        return data[column, row]
    }

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

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        if(listener !in gridChangeListeners){
            gridChangeListeners.add(listener)
        }
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        gridChangeListeners.remove(listener)
    }

    private fun onGridChanged(column: Int, row: Int){
        for(listener in gridChangeListeners){
            listener.onGridChanged(this, column, row)
        }
    }

    private fun isOnGrid(guessCell:Coordinate): Boolean{
        return (guessCell.x < columns && guessCell.y < rows) && (guessCell.x >= 0 && guessCell.y >= 0)
    }



}