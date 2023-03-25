package uk.ac.bournemouth.ap.battleshiplib

//import uk.ac.bournemouth.ap.battleshipslogic.MyGrid

interface BattleShipGame {

    val columns: Int
    val rows: Int

    //fun placeShipsRandom(columns: Int, rows: Int, shipList: IntArray): List<Ship>

    //fun overlap(ship: Ship, ships: MutableList<Ship>, grid:MyGrid): Boolean
}