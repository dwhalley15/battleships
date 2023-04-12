package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
/**
 * A class that represents an opponent player of the grid it is passed into.
 * This version validates the ship placement post initialisation.
 *
 * @author David Whalley
 *
 * @param columns The maximum amount of grid columns.
 * @param rows The maximum amount of grid rows.
 * @param ships The list of ships that have already been placed.
 */
class MySecondOpponent(
    override val columns: Int,
    override val rows: Int,
    override val ships: List<MyShip>
) : BattleshipOpponent {

    /**
     * A function that determines if a ship is present at the given location.
     *
     * @param column The column to check.
     * @param row The row to check.
     *
     * @return Of type MyShip and the index in ships if a ship is present at given location, else null.
     */
    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<MyShip>? {
        val ship = ships.find{it.containsCoordinate(column, row)}
        return if(ship != null){
            val index = ships.indexOf(ship)
            BattleshipOpponent.ShipInfo(index, ship)
        } else{
            null
        }
    }

    /**
     * A function that validates all ships in a given list of ships.
     * If not validated throws an illegal argument exception.
     *
     * @param ships The list of type MyShip to validate.
     */
    private fun validShipPlacement(ships: List<MyShip>){
        for(i in ships.indices){
                if (!(isOnGrid(ships[i].left, ships[i].top) && isOnGrid(ships[i].right, ships[i].bottom))) {
                    throw java.lang.IllegalArgumentException("Ship is not on the grid.")
                }
                if(isOverlapping(ships[i], ships)){
                    throw java.lang.IllegalArgumentException("Ship overlaps another.")
                }
                if(!isShipDirValid(ships[i])){
                    throw java.lang.IllegalArgumentException("Ship has an inverted direction.")
                }
                if(!isShipValid(ships[i])){
                    throw java.lang.IllegalArgumentException("Ship has an invalid shape.")
                }
        }
    }

    /**
     * A function that validates a given ship is placed the correct direction.
     *
     * @param candidate The ship of type MyShip to be validated.
     *
     * @return True or false depending if the ship direction is valid.
     */
    private fun isShipDirValid(candidate: MyShip): Boolean{
        return if(candidate.top > candidate.bottom){
            false
        } else candidate.left <= candidate.right
    }

    /**
     * A function that validates a given ship is the correct shape.
     *
     * @param candidate The ship of type MyShip to be validated.
     *
     * @return True or false depending if the ship shape is valid.
     */
    private fun isShipValid(candidate: MyShip): Boolean{
        if(candidate.top != candidate.bottom){
            if(candidate.left != candidate.right){
                return false
            }
        }
        return true
    }

    /**
     * A function that validates a given ship does not overlap with other ships.
     *
     * @param candidate The ship of type MyShip to be validated.
     * @param ships List of all ships of type MyShip.
     *
     * @return True or false depending if the ship overlaps with another.
     */
    private fun isOverlapping(candidate: MyShip, ships: List<MyShip>): Boolean{
        for(ship in ships) {
            if(ship == candidate) continue
            if (candidate.rowIndices.intersect(ship.rowIndices).isNotEmpty()){
                if(candidate.columnIndices.intersect(ship.columnIndices).isNotEmpty()){
                    return true
                }
            }
        }
        return false
    }

    /**
     * A function that validates a given ship is on the grid.
     *
     * @param column of the ship location.
     * @param row of the ship location.
     *
     * @return True or false depending if the ship is placed off the grid.
     */
    private fun isOnGrid(column: Int, row: Int): Boolean{
        return (column < columns && row < rows) && (column >= 0 && row >= 0)
    }

    /**
     * Runs the validation checks on the list of ships on initilisation.
     */
    init {
        validShipPlacement(ships)
    }
}