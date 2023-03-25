package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.Ship
import kotlin.random.Random

class MyOpponent(
    override val columns: Int,
    override val rows: Int,
    override val ships: List<Ship>
) : BattleshipOpponent {

    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<Ship>? {
        return TODO()
    }



}