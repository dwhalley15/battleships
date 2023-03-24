package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.Ship
import kotlin.random.Random

class MyOpponent(
    override val columns: Int,
    override val rows: Int,
    override val ships: List<Ship>
) : BattleshipOpponent {

    private val placedShips = (mutableListOf <Ship>())

    private val shipList = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )

    //A function to randomly place ships within a grid. THIS IS CURRENTLY WRONG
    fun placeShips(){
        for(ship in shipList){
            do {
                val candidate = object : Ship {
                    //Gets the ship size from the list of ships.
                    override val size = ship
                    // Randomly selects direction 0 = horizontal, 1 = vertical.
                    val direction = Random.nextInt(0, 1)
                    // Randomly picks the top value depending on direction, then works out left, bottom and right depending on top and direction.
                    override val top: Int =
                        if (direction == 0) {
                            Random.nextInt(0, rows - (size-1))
                        }
                    else{
                            Random.nextInt(0, columns - (size-1))
                        }
                    override val left: Int =
                        if (direction == 0) {
                            top
                        }
                        else{
                            top-(size-1)
                        }
                    override val bottom: Int =
                        if (direction == 0) {
                            top + (size - 1)
                        } else {
                            top
                        }
                    override val right: Int =
                        if (direction == 0) {
                            top + (size - 1)
                        } else {
                            top + (size - 1)
                        }
                }
                placedShips.add(candidate)
            }
               while(!overlap(candidate, placedShips))
        }
    }

    // A function that prevents overlapping ships.

    private fun overlap(ship: Ship, grid: MutableList<Ship>): Boolean {
        for (row in ship.rowIndices) {
            for (column in ship.columnIndices) {
                if (grid.contains(ship)) {
                    return false
                }
            }
        }
        for (row in ship.rowIndices) {
            for (column in ship.columnIndices) {
                grid.add(ship)
            }
        }
        return true
    }

    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<Ship>? {
        TODO()
    }

}