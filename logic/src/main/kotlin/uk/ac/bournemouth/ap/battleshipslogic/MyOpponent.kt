package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.Ship
import kotlin.random.Random

class MyOpponent(
    override val columns: Int,
    override val rows: Int,
) : BattleshipOpponent {

    private val shipTypes = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )

    private val grid = MyGrid(columns, rows, this)

    override val ships = placeShipsRandom(columns, rows, shipTypes)

    //A function that places candidate ships on to the grid.
    fun placeShipsOnGrid(ships: List<Ship> ): Array<IntArray>{
        val grid = Array(columns) { IntArray(rows) { 0 } }
        for(ship in ships) {
            for (row in ship.top until ship.bottom+1) {
                for (column in ship.left until ship.right+1)
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



}