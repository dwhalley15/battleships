package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.Ship

class MyShip(
    override val top: Int,
    override val left: Int,
    override val bottom: Int,
    override val right: Int
) : Ship {

    //Returns true if the given column is between left and right and the given row is between top and bottom.
    fun containsCoordinate(column: Int, row: Int): Boolean {
        return column in left..right && row in top..bottom
    }

    //Records any hits to this ship
    var hits = 0

    //Returns true if hits equal size.
    val isSunk: Boolean
        get() = hits == size

/* THIS IS OLD AND CAN BE DELETED LATER
    private val shipTypes = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )

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

    // A function that prevents overlapping ships.
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
*/

}