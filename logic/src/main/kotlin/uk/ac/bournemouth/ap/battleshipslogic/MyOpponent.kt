package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.Ship
import kotlin.random.Random

class MyOpponent(
    override val columns: Int,
    override val rows: Int,
) : BattleshipOpponent {

    val shipTypes = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )

    var blueSunk = 0
    var redSunk = 0

    private val grid = MyGrid(columns, rows, this)

    override val ships = placeShipsRandom(columns, rows, shipTypes)

    //A function that places candidate ships on to the grid.
    fun placeShipsOnGrid(ships: List<Ship> ): Array<IntArray>{
        val grid = Array(columns) { IntArray(rows) { 0 } }
        for(ship in ships) {
            for (column in ship.top until ship.bottom+1) {
                for (row in ship.left until ship.right+1)
                    grid[column][row] = ship.size
            }
        }
        return grid
    }

    //A function that randomly picks where a ship can be placed.
    private fun placeShipsRandom(columns: Int, rows: Int, shipTypes: IntArray): List<Ship> {
        val ships = (mutableListOf <Ship>())
        for(ship in shipTypes){
            do {
                val candidate = object : Ship {
                    //Gets the ship size from the list of ships.
                    val length = ship
                    // Randomly selects direction 0 = horizontal, 1 = vertical.
                    val direction = Random.nextInt(0, 2)
                    // Randomly picks the top value depending on direction, then works out left, bottom and right depending on top and direction.
                    override val top: Int =
                        if (direction == 0) {
                            Random.nextInt(0, rows)
                        }
                        else{
                            Random.nextInt(0, rows - length)
                        }
                    override val left: Int =
                        if (direction == 0) {
                            Random.nextInt(0, columns - length)
                        }
                        else{
                            Random.nextInt(0, columns)
                        }
                    override val bottom: Int =
                        if (direction == 0) {
                            top
                        } else {
                            top + (length- 1)
                        }
                    override val right: Int =
                        if (direction == 0) {
                            left + (length - 1)
                        } else {
                            left
                        }
                }
            }
            while(!overlap(candidate, ships))
        }
        return ships
    }

    // A function that prevents overlapping ships. CURRENTLY BROKEN
    private fun overlap(candidate: Ship, ships: MutableList<Ship>): Boolean {

        for(ship in ships){
            if(candidate.rowIndices.intersect(ship.rowIndices).isNotEmpty()){
                if(candidate.columnIndices.intersect(ship.columnIndices).isNotEmpty()){
                    return false
                }
            }
        }
        ships.add(candidate)
        return true
    }


    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<Ship>? {
        return TODO()
    }

    //Blue players turn (blue player is the computer)
    fun blueTurn(columns:Int, rows:Int, playerGrid:Array<IntArray>, ships: List<Ship>): Array<IntArray>{
        var guessCell = mutableListOf<Int>()
        do {
            guessCell = randomGuess(columns, rows)
        }
        while(!isGuessValid(guessCell, playerGrid))

        if(isGuessHit(guessCell, playerGrid)){
            playerGrid[guessCell[0]][guessCell[1]] = 6 //hit
                blueSunk = isSunk(ships, playerGrid, blueSunk)
        }
        else  if(playerGrid[guessCell[0]][guessCell[1]] == 0){
            playerGrid[guessCell[0]][guessCell[1]] = 1 //miss
        }
        return playerGrid
    }

    //Guess a cell by randomly selecting a column and row int.
    fun randomGuess(columns: Int, rows: Int): MutableList<Int> {
        val guessColumn = Random.nextInt(0, columns)
        val guessRow = Random.nextInt(0, rows)
        return mutableListOf(guessColumn, guessRow)
    }

    //Check that a selected cell has not been shot at before.
    fun isGuessValid(guessCell:MutableList<Int>, playerGrid:Array<IntArray>): Boolean{
        return playerGrid[guessCell[0]][guessCell[1]] != 1 && playerGrid[guessCell[0]][guessCell[1]] != 6 && playerGrid[guessCell[0]][guessCell[1]] != 7
    }

    fun isGuessHit(guessCell:MutableList<Int>, playerGrid:Array<IntArray>): Boolean{
        return playerGrid[guessCell[0]][guessCell[1]] == 2 || playerGrid[guessCell[0]][guessCell[1]] == 3 || playerGrid[guessCell[0]][guessCell[1]] ==  4 || playerGrid[guessCell[0]][guessCell[1]] ==  5
    }


    //Confirms if a ship has been sunk.
    fun isSunk(ships: List<Ship>, grid: Array<IntArray>, sunk: Int): Int{
        var sunken = sunk
        for(ship in ships){
            var isHit = 0
            for(column in ship.top until ship.bottom+1){
                for(row in ship.left until ship.right+1){
                    if(grid[column][row] == 6){
                        isHit += 1
                    }
                }
            }
            if(isHit == ship.size){
                for(column in ship.top until ship.bottom+1){
                    for(row in ship.left until ship.right+1){
                        grid[column][row] = 7
                    }
                }
                sunken += 1
            }
        }
        return sunken
    }


    //For debug purposes should be removed later for human player turn
    fun redTurn(columns: Int, rows: Int, playerGrid: Array<IntArray>, ships: List<Ship>): Array<IntArray>{
        var guessCell = mutableListOf<Int>()
        do {
            guessCell = randomGuess(columns, rows)
        }
        while(!isGuessValid(guessCell, playerGrid))

        if(isGuessHit(guessCell, playerGrid)){
            playerGrid[guessCell[0]][guessCell[1]] = 6 //hit
            redSunk = isSunk(this.ships, playerGrid, redSunk)
        }
        else if(playerGrid[guessCell[0]][guessCell[1]] == 0){
            playerGrid[guessCell[0]][guessCell[1]] = 1 //miss
        }
        return playerGrid
    }




}