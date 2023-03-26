package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleShipGame

class MyBattleShipGame(override val columns: Int, override val rows: Int) : BattleShipGame {


    private val redPlayer = MyOpponent(columns, rows)

    val redPlayerGrid = redPlayer.placeShipsOnGrid(redPlayer.ships)

    private val bluePLayer = MyOpponent(columns, rows)

    val bluePlayerGrid = bluePLayer.placeShipsOnGrid(bluePLayer.ships)


}