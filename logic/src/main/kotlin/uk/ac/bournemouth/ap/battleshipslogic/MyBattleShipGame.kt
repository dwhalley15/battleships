package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleShipGame
import uk.ac.bournemouth.ap.battleshiplib.Ship
import kotlin.random.Random

class MyBattleShipGame(override val columns: Int, override val rows: Int) : BattleShipGame {

    private val shipList = intArrayOf(
        5, // Carrier
        4, // Battleship"
        3, // Cruiser"
        3, // Submarine"
        2 // Destroyer
    )

    val redPlayer = MyOpponent(columns, rows, placeShipsRandom(columns, rows, shipList))

    val redPlayerGrid = MyGrid(columns, rows, redPlayer)

    val createdRedPlayerGrid = redPlayerGrid.createGrid(columns, rows)

    fun placeShipsOnGrid(ships: List<Ship>, grid: Array<IntArray> ): Boolean{
        for(ship in ships) {
            for (row in ship.top until ship.bottom+1) {
                for (column in ship.left until ship.right+1)
                    grid[column][row] = ship.size
            }
        }
        return true
    }

    val bluePlayer = MyOpponent(columns, rows, placeShipsRandom(columns, rows, shipList))

    val bluePlayerGrid = MyGrid(columns, rows, bluePlayer)

    val createdBluePlayerGrid = bluePlayerGrid.createGrid(columns, rows)

    //Function to randomly place ships on the grid.
    override fun placeShipsRandom(columns: Int, rows: Int, shipList: IntArray): List<Ship> {
        val ships = (mutableListOf <Ship>())
        for(ship in shipList){
            do {
                val candidate = object : Ship {
                    //Gets the ship size from the list of ships.
                    val length = ship
                    // Randomly selects direction 0 = horizontal, 1 = vertical.
                    val direction = Random.nextInt(0, 1)
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
    override fun overlap(ship: Ship, grid: MutableList<Ship>): Boolean {
        for (row in ship.rowIndices) {
            for (column in ship.columnIndices) {
                if (grid.contains(ship)) {
                    return false
                }
            }
        }
        grid.add(ship)
        return true
    }

}