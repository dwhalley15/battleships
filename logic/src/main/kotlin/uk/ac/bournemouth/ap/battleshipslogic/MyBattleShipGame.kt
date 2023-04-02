package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random


class MyBattleShipGame(val columns: Int, val rows: Int){


    val redPlayer = MyOpponent(columns, rows)

    val bluePlayer = MyOpponent(columns, rows)

    var redGrid = MyGrid(columns, rows, bluePlayer)

    var blueGrid = MyGrid(columns, rows, redPlayer)

    var turn:Int = Random.nextInt(2)+1


    //Plays a computer turn
    fun playTurn(columns: Int, rows: Int, grid: MyGrid){
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