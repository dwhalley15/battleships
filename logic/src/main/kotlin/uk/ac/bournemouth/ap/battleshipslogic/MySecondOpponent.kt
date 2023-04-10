package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

class MySecondOpponent(
    override val columns: Int,
    override val rows: Int,
    override val ships: List<MyShip>
) : BattleshipOpponent {

    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<MyShip>? {
        val ship = ships.find{it.containsCoordinate(column, row)}
        return if(ship != null){
            val index = ships.indexOf(ship)
            BattleshipOpponent.ShipInfo(index, ship)
        } else{
            null
        }
    }

    //A function that checks the passed in list of ships do not overlap and do not go off the grid.
    //When implemented this works and testInvalidShips test passes, but it breaks 4 other tests that work without this.
    private fun validShipPlacement(ships: List<MyShip>){
        for(i in 0..ships.size){
            if(!isOnGrid(ships[i].topLeft) || !isOnGrid(ships[i].bottomRight)){
                throw java.lang.IllegalArgumentException("Ship is not on the grid.")
                }
            if(ships[i].rowIndices.intersect(ships[i+1].rowIndices).isNotEmpty()){
                if(ships[i].columnIndices.intersect(ships[i+1].columnIndices).isNotEmpty()){
                    throw java.lang.IllegalArgumentException("Ships overlap.")
                }
            }
        }
    }

    private fun isOnGrid(guessCell: Coordinate): Boolean{
        return (guessCell.x < columns && guessCell.y < rows) && (guessCell.x >= 0 && guessCell.y >= 0)
    }

    init {
        //validShipPlacement(ships)
    }

}