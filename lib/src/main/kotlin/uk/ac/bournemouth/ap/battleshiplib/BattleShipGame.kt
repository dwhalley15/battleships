package uk.ac.bournemouth.ap.battleshiplib

interface BattleShipGame {

    val columns: Int
    val rows: Int

    fun placeShipsRandom(columns: Int, rows: Int, shipList: IntArray): List<Ship>

    fun overlap(ship: Ship, grid: MutableList<Ship>): Boolean
}