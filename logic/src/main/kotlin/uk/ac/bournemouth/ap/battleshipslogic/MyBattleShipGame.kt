package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleShipGame

class MyBattleShipGame(override val columns: Int, override val rows: Int) : BattleShipGame {


    private val redPlayer = MyOpponent(columns, rows)

    val redPlayerGrid = redPlayer.placeShipsOnGrid(redPlayer.ships)

    private val bluePLayer = MyOpponent(columns, rows)

    val bluePlayerGrid = bluePLayer.placeShipsOnGrid(bluePLayer.ships)


    //Plays a turn.
    fun playTurn(turn: Int): Array<IntArray> {
        return bluePLayer.blueTurn(columns, rows, bluePlayerGrid, bluePLayer.ships)
    }

    //Confirms if the game is over.
    fun isGameOver(sunk:Int): Boolean{
        return sunk == redPlayer.shipTypes.size
    }


}