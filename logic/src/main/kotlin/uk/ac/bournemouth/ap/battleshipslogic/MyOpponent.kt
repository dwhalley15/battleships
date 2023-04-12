package uk.ac.bournemouth.ap.battleshipslogic

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.random.Random
/**
 *A class that represents an opponent player of the grid it is passed into.
 *
 * @author David Whalley
 *
 * @param columns The maximum amount of grid columns.
 * @param rows The maximum amount of grid rows.
 * @param shipTypes The list of ship types. Each ship represented by an integer depicting the length.
 * @param random A random abstract class.
 */
class MyOpponent(
    override val columns: Int,
    override val rows: Int,
    val shipTypes: IntArray,
    random: Random
) : BattleshipOpponent {

    /**
     * A list of ships of type MyShip.
     * Used by another opponent to shoot at.
     */
    override val ships = randomShipPlacement(columns, rows, shipTypes, random)

    /**
     * Creates a list of tacitcs of type Coordinate.
     * Used to make tactical guesses
     */
    val tactics = mutableListOf<Coordinate>()

    /**
     * Creates a list of hits of type Coordinate.
     * Used to make generate tactics.
     */
    var hits = mutableListOf<Coordinate>()

    /**
     * A function that generates a list of ships of type MyShip.
     * Ships are placed randomly.
     *
     * @param columns The maximum amount of grid columns.
     * @param rows The maximum amount of grid rows.
     * @param shipTypes The list of ship types. Each ship represented by an integer depicting the length.
     * @param random A random abstract class.
     *
     * @return List of type MyShip.
     */
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
                        val end = if(ship == rows){ 1 } else{ rows - ship }
                        random.nextInt(0, end)
                    }
                val left: Int =
                    if (direction == 0) {
                        val end = if(ship == columns){ 1 } else{ columns - ship }
                        random.nextInt(0, end)
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

    /**
     * A function that if a given ship overlaps with an existing ship in the given list of ships.
     * Adds a non-overlapping ship to the existing list of ships.
     *
     * @param candidate The ship of type MyShip to check.
     * @param ships The current list of placed ships of type MyShip.
     *
     * @return True or false depending on if the given ship overlaps with another.
     */
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
        }
        else{
            null
        }
    }
}