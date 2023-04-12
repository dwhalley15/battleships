package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent


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
    private fun validShipPlacement(ships: List<MyShip>){
        for(i in ships.indices){
                if (!(isOnGrid(ships[i].left, ships[i].top) && isOnGrid(ships[i].right, ships[i].bottom))) {
                    throw java.lang.IllegalArgumentException("Ship is not on the grid.")
                }
                if(isOverlapping(ships[i], ships)){
                    throw java.lang.IllegalArgumentException("Ships overlap.")
                }
        }
    }

    private fun isOverlapping(candidate: MyShip, ships: List<MyShip>): Boolean{
        for(ship in ships) {
            if(ship == candidate) continue
            if (candidate.rowIndices.intersect(ship.rowIndices).isNotEmpty()){
                if(candidate.columnIndices.intersect(ship.columnIndices).isNotEmpty()){
                    return true
                }
            }
        }
        return false
    }

    private fun isOnGrid(column: Int, row: Int): Boolean{
        return (column < columns && row < rows) && (column >= 0 && row >= 0)
    }

    init {
        validShipPlacement(ships)
    }

}