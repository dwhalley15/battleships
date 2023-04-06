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
        }
        else{
            null
        }
    }
}