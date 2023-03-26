package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult

class MyGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: BattleshipOpponent
) : BattleshipGrid{

    override val shipsSunk: BooleanArray
        get() = TODO("Not yet implemented")

    override fun get(column: Int, row: Int): GuessCell {
        TODO("Not yet implemented")
    }

    override fun shootAt(column: Int, row: Int): GuessResult {
        TODO("Not yet implemented")
    }

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        TODO("Not yet implemented")
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        TODO("Not yet implemented")
    }
}