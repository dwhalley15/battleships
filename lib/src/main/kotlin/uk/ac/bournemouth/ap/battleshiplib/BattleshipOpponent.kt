package uk.ac.bournemouth.ap.battleshiplib

import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

interface BattleshipOpponent {
    /**
     * The amount of columns in the game grid (how wide is the grid).
     */
    val columns: Int

    /**
     * The amount of rows in the game grid (how high is the grid)
     */
    val rows: Int

    /**
     * Get the list of ships for this opponent
     */
    val ships: List<Ship>

    /**
     * Determine the ship at the given position. Note that subclasses could specialise the return
     * type to be a shipInfo of a subtype of Ship.
     */
    fun shipAt(column: Int, row: Int): ShipInfo<Ship>?

    /**
     * Simple class to hold information about a ship, both the index and the ship itself
     */
    data class ShipInfo<out S: Ship>(val index: Int, val ship: S)
}

/**
 * Shortcut function that will allow accessing the shipAt function with a coordinate directly.
 */
fun BattleshipOpponent.shipAt(coordinate: Coordinate): BattleshipOpponent.ShipInfo<Ship>? =
    shipAt(coordinate.x, coordinate.y)