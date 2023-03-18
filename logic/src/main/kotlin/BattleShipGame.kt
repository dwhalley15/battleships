import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.lib.matrix.Matrix

class BattleShipGame(
    override val columns: Int,
    override val rows: Int,
    override val opponent: BattleshipOpponent,
    override val shipsSunk: BooleanArray
): BattleshipGrid {


    //private val grid: Matrix<Cell> = Matrix(9, 9, ::Cell)

    private var data: Array<IntArray> = Array(columns) { IntArray(rows) { 0 } }

    var playerTurn: Int = 1
        private set

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