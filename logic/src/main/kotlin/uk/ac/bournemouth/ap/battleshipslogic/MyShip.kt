package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.Ship
/**
 *A class that represents a ship.
 *
 * @author David Whalley
 *
 * @param top The top of a ship location.
 * @param left The left of a ship locatoin.
 * @param bottom The bottom of a ship location.
 * @param right The right of a ship location.
 */
class MyShip(
    override val top: Int,
    override val left: Int,
    override val bottom: Int,
    override val right: Int
) : Ship {

    /**
     * Records the amount of hits this ship has sustained.
     */
    var hits = 0

    /**
     * Confirms if this ship has been sunk.
     *
     * @return True or false depending if the amount of hits is equal to the ship size.
     */
    val isSunk: Boolean
        get() = hits == size

    /**
     * A function to see if part of this ship is located in the given cell location.
     *
     * @param column of the given cell.
     * @param row of the given cell.
     *
     * @return True or false depending on if the given cell exists within the ship location.
     */
    fun containsCoordinate(column: Int, row: Int): Boolean {
        return column in left..right && row in top..bottom
    }
}