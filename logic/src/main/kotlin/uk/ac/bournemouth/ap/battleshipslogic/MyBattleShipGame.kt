package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random


class MyBattleShipGame(val columns: Int, val rows: Int){

    private val shipTypes = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )

    val redPlayer = MyOpponent(columns, rows, shipTypes, Random)

    val bluePlayer = MyOpponent(columns, rows, shipTypes, Random)

    var redGrid = MyGrid(columns, rows, bluePlayer)

    var blueGrid = MyGrid(columns, rows, redPlayer)

    var turn:Int = Random.nextInt(2)+1


    //This plays a one player game with redplayer being the human player, blueplayer being the computer.
    fun playerGame(column: Int, row: Int){
        if(column < columns && row < rows){
            if(blueGrid.isGuessValid(Coordinate(column, row))) {
                blueGrid.shootAt(column, row)
                blueGrid.onGridChanged(column, row)
                playTurn(columns, rows, redGrid)
                redGrid.onGridChanged(column, row)
            }
        }

    }

    //Plays a computer turn
    private fun playTurn(columns: Int, rows: Int, grid: MyGrid){
        var guessCell: Coordinate
        do {
            guessCell = grid.randomGuess(columns, rows, Random)
        }
            while(!grid.isGuessValid(guessCell))
        grid.shootAt(guessCell.x, guessCell.y)
        turn = (turn % 2)+1
    }


    //This plays a game mode computer vs computer
    fun playGame(){
        if(turn == 1){
            playTurn(columns, rows, blueGrid)
        }
        else{
            playTurn(columns, rows, redGrid)
        }
    }

    interface OnGameFinishedListener {
        fun onGameFinished()
    }

    /*THIS IS OLD AND CAN BE DELETED LATER.
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
    }*/



}