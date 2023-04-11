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
        for(i in ships.indices){
                if (!(isOnGrid(ships[i].left, ships[i].top) && isOnGrid(ships[i].right, ships[i].bottom))) {
                    throw java.lang.IllegalArgumentException("Ship is not on the grid.")
                }
            //This needs work..
            /*if(ships[i].rowIndices.intersect(ships[i+1].rowIndices).isNotEmpty()){
                if(ships[i].columnIndices.intersect(ships[i+1].columnIndices).isNotEmpty()){
                    throw java.lang.IllegalArgumentException("Ships overlap.")
                }
            }*/
        }
    }

    private fun isOnGrid(column: Int, row: Int): Boolean{
        return (column < columns && row < rows) && (column >= 0 && row >= 0)
    }

    init {
        validShipPlacement(ships)
    }

}