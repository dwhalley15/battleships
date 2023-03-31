package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleShipGame

class MyBattleShipGame(override val columns: Int, override val rows: Int) : BattleShipGame {


    val redPlayer = MyOpponent(columns, rows)

    var redPlayerGrid = redPlayer.placeShipsOnGrid(redPlayer.ships)

    val bluePlayer = MyOpponent(columns, rows)

    var bluePlayerGrid = bluePlayer.placeShipsOnGrid(bluePlayer.ships)

    private var turn:Int = 1

    //Plays a turn.
    fun playTurn(): Array<IntArray> {
        return if(turn == 1){
            redPlayerGrid = redPlayer.redTurn(columns, rows, redPlayerGrid, redPlayer.ships)
            turn = 2
            redPlayerGrid
        } else{
            bluePlayerGrid = bluePlayer.blueTurn(columns, rows, bluePlayerGrid, bluePlayer.ships)
            turn = 1
            bluePlayerGrid
        }
    }

    //Confirms if the game is over.
    fun isGameOver(player: MyOpponent, sunk:Int): Boolean{
        return sunk == player.shipTypes.size
    }


}