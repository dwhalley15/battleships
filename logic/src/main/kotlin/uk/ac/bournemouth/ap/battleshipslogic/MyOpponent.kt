package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random

class MyOpponent(
    override val columns: Int,
    override val rows: Int,
    val shipTypes: IntArray,
    random: Random
) : BattleshipOpponent {

    override val ships = randomShipPlacement(columns, rows, shipTypes, random)

    val tactics = mutableListOf<Coordinate>()

    var firstHit = mutableListOf<Coordinate>()

    var secondHit = mutableListOf<Coordinate>()

    //A random place ship function that brings in type random and returns List of type MyShip
    private fun randomShipPlacement(columns: Int, rows: Int, shipTypes: IntArray, random: Random): List<MyShip>{
        val ships = (mutableListOf<MyShip>())
        for(ship in shipTypes){
            do{
                val direction = random.nextInt(0, 2)
                val top: Int =
                    if (direction == 0) {
                        random.nextInt(0, rows)
                    }
                    else{
                        random.nextInt(0, rows - ship)
                    }
                val left: Int =
                    if (direction == 0) {
                        random.nextInt(0, columns - ship)
                    }
                    else{
                        random.nextInt(0, columns)
                    }
                val bottom: Int =
                    if (direction == 0) {
                        top
                    } else {
                        top + (ship - 1)
                    }
                val right: Int =
                    if (direction == 0) {
                        left + (ship - 1)
                    } else {
                        left
                    }
                val candidate = MyShip(top, left, bottom, right)
            }
            while(!overlapping(candidate, ships))
        }
        return ships
    }

    //New version of overlap the same just the type is MyShip not Ship
    private fun overlapping(candidate: MyShip, ships: MutableList<MyShip>): Boolean{
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

    //Determines what ship is at a position given a column and row. If no ship is present returns null.
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

    /*val shipTypes = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )*/

    /*OLD TO BE DELETED
    fun placeShipsOnGrid(ships: List<Ship> ): Array<IntArray>{
        val grid = Array(columns) { IntArray(rows) { 0 } }
        for(ship in ships) {
            for (column in ship.top until ship.bottom+1) {
                for (row in ship.left until ship.right+1)
                    grid[column][row] = ship.size
            }
        }
        return grid
    }*/

    /*THIS IS AN OLD VERSION THAT MAY BE DELETED LATER
    private fun placeShipsRandom(columns: Int, rows: Int, shipTypes: IntArray, random: Random): List<Ship> {
        val ships = (mutableListOf <Ship>())
        for(ship in shipTypes){
            do {
                val candidate = object : Ship {
                    //Gets the ship size from the list of ships.
                    val length = ship
                    // Randomly selects direction 0 = horizontal, 1 = vertical.
                    val direction = random.nextInt(0, 2)
                    // Randomly picks the top value depending on direction, then works out left, bottom and right depending on top and direction.
                    override val top: Int =
                        if (direction == 0) {
                            random.nextInt(0, rows)
                        }
                        else{
                            random.nextInt(0, rows - length)
                        }
                    override val left: Int =
                        if (direction == 0) {
                            random.nextInt(0, columns - length)
                        }
                        else{
                            random.nextInt(0, columns)
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

    //THIS IS AN OLD VERSION THAT MAY BE DELETED LATER
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



    //Blue players turn (blue player is the computer)
    fun blueTurn(columns:Int, rows:Int, playerGrid:Array<IntArray>, ships: List<Ship>): Array<IntArray>{
        var guessCell: Coordinate
        do {
            guessCell = randomGuess(columns, rows)
        }
        while(!isGuessValid(guessCell, playerGrid))

        if(isGuessHit(guessCell, playerGrid)){
            playerGrid[guessCell.x][guessCell.y] = 6 //hit
                blueSunk = isSunk(ships, playerGrid, blueSunk)
        }
        else  if(playerGrid[guessCell.x][guessCell.y] == 0){
            playerGrid[guessCell.x][guessCell.y] = 1 //miss
        }
        return playerGrid
    }

    //Guess a cell by randomly selecting a column and row int.
    fun randomGuess(columns: Int, rows: Int): Coordinate {
        val guessColumn = Random.nextInt(0, columns)
        val guessRow = Random.nextInt(0, rows)
        return Coordinate(guessColumn, guessRow)
    }

    //Check that a selected cell has not been shot at before.
    fun isGuessValid(guessCell:Coordinate, playerGrid:Array<IntArray>): Boolean{
        return playerGrid[guessCell.x][guessCell.y] != 1 && playerGrid[guessCell.x][guessCell.y] != 6 && playerGrid[guessCell.x][guessCell.y] != 7
    }

    fun isGuessHit(guessCell:Coordinate, playerGrid:Array<IntArray>): Boolean{
        return playerGrid[guessCell.x][guessCell.y] == 2 || playerGrid[guessCell.x][guessCell.y] == 3 || playerGrid[guessCell.x][guessCell.y] ==  4 || playerGrid[guessCell.x][guessCell.y] ==  5
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
    fun redTurn(columns:Int, rows:Int, playerGrid:Array<IntArray>, ships: List<Ship>): Array<IntArray>{
        var guessCell: Coordinate
        do {
            guessCell = randomGuess(columns, rows)
        }
        while(!isGuessValid(guessCell, playerGrid))

        if(isGuessHit(guessCell, playerGrid)){
            playerGrid[guessCell.x][guessCell.y] = 6 //hit
            redSunk = isSunk(ships, playerGrid, redSunk)
        }
        else  if(playerGrid[guessCell.x][guessCell.y] == 0){
            playerGrid[guessCell.x][guessCell.y] = 1 //miss
        }
        return playerGrid
    }*/




}