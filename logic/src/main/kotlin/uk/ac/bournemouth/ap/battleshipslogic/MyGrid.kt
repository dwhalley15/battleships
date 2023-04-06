package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.GuessResult
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random

class MyGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: MyOpponent
) : BattleshipGrid{

    val data = MutableMatrix<GuessCell>(columns, rows) {col, row -> GuessCell.UNSET }

    override val shipsSunk = BooleanArray(opponent.shipTypes.size)

    private val gridChangeListeners = mutableListOf<BattleshipGrid.BattleshipGridListener>()


    override fun get(column: Int, row: Int): GuessCell {
        return data[column, row]
    }

    override fun shootAt(column: Int, row: Int): GuessResult {
        opponent.tactics.remove(Coordinate(column, row))
        return if(opponent.shipAt(column, row) != null){
            //onHit(Coordinate(column, row))
            val ship = opponent.shipAt(column, row)!!.ship
            val index = opponent.shipAt(column, row)!!.index
            data[column, row] = GuessCell.HIT(index)
            //generateTactics(column, row)
            if(checkIfSunk(ship)){
                for(col in ship.left..ship.right){
                    for(r in ship.top..ship.bottom){
                        data[col, r] = GuessCell.SUNK(index)
                    }
                }
                opponent.tactics.clear()
                opponent.firstHit.clear()
                opponent.secondHit.clear()
                shipsSunk[index] = true
                GuessResult.SUNK(index)
            }
            GuessResult.HIT(index)
        } else{
            data[column, row] = GuessCell.MISS
            GuessResult.MISS
        }
    }

    //Guess a cell by randomly selecting a column and row int.
    fun randomGuess(columns: Int, rows: Int, random: Random): Coordinate {
        val guessColumn = random.nextInt(0, columns)
        val guessRow = random.nextInt(0, rows)
        return Coordinate(guessColumn, guessRow)
    }

    //Guess a cell randomly that exists inside the tactics list.
    fun tacticalGuess(tactics: MutableList<Coordinate>, random: Random): Coordinate {
        val randomIndex = random.nextInt(tactics.size)
        return tactics[randomIndex]
    }

    //Check that a selected cell is on the grid.
    private fun isOnGrid(guessCell:Coordinate): Boolean{
        return (guessCell.x < columns && guessCell.y < rows) && (guessCell.x >= 0 && guessCell.y >= 0)
    }

    //Check that a selected cell has not been shot at before.
    fun isGuessValid(guessCell:Coordinate): Boolean{
        return data[guessCell.x, guessCell.y] == GuessCell.UNSET
    }

    //Adds to a ships hits value returns true if a ship as been sunk
    private fun checkIfSunk(ship: MyShip): Boolean{
        ship.hits++
        return ship.isSunk
    }

    //Adds the guess to either the firstHit or secondHit
    private fun onHit(guessCell: Coordinate){
        if(opponent.firstHit.isEmpty()){
            opponent.firstHit.add(guessCell)
        }
        else if(opponent.secondHit.isEmpty()){
            opponent.secondHit.add(guessCell)
        }
    }

    //Generates tactics based on what is stored in firstHit and secondHit
    private fun generateTactics(column: Int, row: Int){
        var direction = ""
        if(opponent.firstHit.isNotEmpty() && opponent.secondHit.isEmpty()){
            val potentialTactics = mutableListOf(Coordinate((column+1), row), Coordinate(column, (row+1)), Coordinate((column-1), row), Coordinate(column, (row-1)))
            val newTactics = mutableListOf<Coordinate>()
            for (tactic in potentialTactics){
                if(isOnGrid(tactic) && isGuessValid(tactic)){
                    newTactics.add(tactic)
                }
            }
            opponent.tactics.clear()
            opponent.tactics.addAll(newTactics)
        }
        else if(opponent.secondHit.isNotEmpty()){
            if(opponent.secondHit[0].x == opponent.firstHit[0].x+1 || opponent.secondHit[0].x == opponent.firstHit[0].x-1 || opponent.secondHit[0].x == opponent.firstHit[0].x-2 || opponent.secondHit[0].x == opponent.firstHit[0].x+2){
                direction = "horizontal"
            }
            else if(opponent.secondHit[0].y == opponent.firstHit[0].y+1 || opponent.secondHit[0].y == opponent.firstHit[0].y-1 || opponent.secondHit[0].y == opponent.firstHit[0].y-2 || opponent.secondHit[0].y == opponent.firstHit[0].y+2){
                direction = "vertical"
            }
            val potentialTactics: MutableList<Coordinate> = if(direction == "horizontal"){
                mutableListOf(Coordinate(opponent.secondHit[0].x+2, opponent.secondHit[0].y), Coordinate(opponent.secondHit[0].x-2, opponent.secondHit[0].y), Coordinate(opponent.secondHit[0].x+1, opponent.secondHit[0].y), Coordinate(opponent.secondHit[0].x-1, opponent.secondHit[0].y))
            } else{
                mutableListOf(Coordinate(opponent.secondHit[0].x, opponent.secondHit[0].y+2), Coordinate(opponent.secondHit[0].x, opponent.secondHit[0].y-2), Coordinate(opponent.secondHit[0].x, opponent.secondHit[0].y+1), Coordinate(opponent.secondHit[0].x, opponent.secondHit[0].y-1))
            }
            val newTactics = mutableListOf<Coordinate>()
            for (tactic in potentialTactics){
                if(isOnGrid(tactic) && isGuessValid(tactic)){
                    newTactics.add(tactic)
                }
            }
            opponent.tactics.clear()
            opponent.tactics.addAll(newTactics)
            opponent.firstHit.clear()
            opponent.firstHit.add(opponent.secondHit[0])
            opponent.secondHit.clear()
        }
    }

    fun isAnyHits(){
        opponent.hits.clear()
        opponent.tactics.clear()
        for(col in 0 until columns){
            for(row in 0 until rows) {
                if (data[col, row] == GuessCell.HIT(0) || data[col, row] == GuessCell.HIT(1) || data[col, row] == GuessCell.HIT(2) || data[col, row] == GuessCell.HIT(3) || data[col, row] == GuessCell.HIT(4)) {
                    val hit = Coordinate(col, row)
                    opponent.hits.add(hit)
                }
            }
        }
        if(opponent.hits.isNotEmpty()){
            huntTactics()
        }
    }

    fun huntTactics(){
        do{
            val index = Random.nextInt(opponent.hits.size)
            val potentialTactics = mutableListOf(Coordinate(opponent.hits[index].x+1, opponent.hits[index].y), Coordinate(opponent.hits[index].x-1,opponent.hits[index].y), Coordinate( opponent.hits[index].x, opponent.hits[index].y+1), Coordinate(opponent.hits[index].x, opponent.hits[index].y-1))
            val newTactics = mutableListOf<Coordinate>()
            for (tactic in potentialTactics){
                if(isOnGrid(tactic) && isGuessValid(tactic)){
                    newTactics.add(tactic)
                }
            }
            opponent.tactics.clear()
            opponent.tactics.addAll(newTactics)
        }
            while(opponent.tactics.isEmpty())
    }

    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        if(listener !in gridChangeListeners){
            gridChangeListeners.add(listener)
        }
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        gridChangeListeners.remove(listener)
    }

    fun onGridChanged(column: Int, row: Int){
        for(listener in gridChangeListeners){
            listener.onGridChanged(this, column, row)
        }
    }

    /* THIS IS OLD AND MAY BE DELETED LATER.
    override fun shootAt(column: Int, row: Int): GuessResult {
        opponent.tactics.remove(Coordinate(column, row))
        return if(opponent.shipAt(column, row) != null){
            val ship = opponent.shipAt(column, row)!!.ship
            val index = opponent.shipAt(column, row)!!.index
            data[column, row] = GuessCell.HIT(index)
            val potentialTactics = mutableListOf(Coordinate((column+1), row), Coordinate(column, (row+1)), Coordinate((column-1), row), Coordinate(column, (row-1)))
            val newTactics = mutableListOf<Coordinate>()
            for (tactic in potentialTactics){
                if(isOnGrid(tactic) && isGuessValid(tactic)){
                    newTactics.add(tactic)
                }
            }
            opponent.tactics.clear()
            opponent.tactics.addAll(newTactics)
            if(checkIfSunk(ship)){
                for(col in ship.left..ship.right){
                    for(r in ship.top..ship.bottom){
                        data[col, r] = GuessCell.SUNK(index)
                    }
                }
                opponent.tactics.clear()
                shipsSunk[index] = true
                GuessResult.SUNK(index)
            }
            GuessResult.HIT(index)
        } else{
            data[column, row] = GuessCell.MISS
            GuessResult.MISS
        }
    }*/
}