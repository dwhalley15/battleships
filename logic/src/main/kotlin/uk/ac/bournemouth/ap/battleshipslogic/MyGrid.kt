package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.battleshiplib.Ship
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random

class MyGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: MyOpponent
) : BattleshipGrid{

    val data = MutableMatrix<GuessCell>(columns, rows) {col, row -> GuessCell.UNSET }

    /* Used for debug purposes
    init{
        data[5,5] = GuessCell.MISS
        data[6,6] = GuessCell.HIT(0)
        data[7,7] = GuessCell.SUNK(0)
    }*/

    override val shipsSunk = BooleanArray(opponent.shipTypes.size)


    override fun get(column: Int, row: Int): GuessCell {
        return data[column, row]
    }

    override fun shootAt(column: Int, row: Int): GuessResult {
        return if(opponent.shipAt(column, row) != null){
            val ship = opponent.shipAt(column, row)!!.ship
            val index = opponent.shipAt(column, row)!!.index
            data[column, row] = GuessCell.HIT(index)
            if(isSunk(ship, index)){
                for(col in ship.top..ship.bottom){
                    for(r in ship.left..ship.right){
                        data[col, r] = GuessCell.SUNK(index)
                    }
                }
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

    //This is currently broken maybe use a different method using the index instead.
    fun isSunk(ship: MyShip, index: Int): Boolean{
        var isHit = 0
            for(column in ship.top..ship.bottom){
                for(row in ship.left..ship.right){
                    if(data[column, row] != GuessCell.UNSET ){
                        isHit += 1
                    }
                }
            }
        return isHit == ship.size
    }

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        TODO("Not yet implemented")
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        TODO("Not yet implemented")
    }
}