package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.battleshiplib.test.BattleshipTest
import uk.ac.bournemouth.ap.lib.matrix.boolean.BooleanMatrix
import kotlin.random.Random

class StudentBattleshipTest : BattleshipTest</*YourShipType*/Ship>() {
    override fun createOpponent(
        columns: Int,
        rows: Int,
        ships: List</*YourShipType*/Ship>
    ): /*YourOpponentType*/BattleshipOpponent {
        return TODO("Create an instance of StudentBattleshipOpponent with the dimensions and ships")
    }

    override fun transformShip(sourceShip: Ship): /*YourShipType*/Ship {
        return TODO("create an instance of StudentShip that maps the given ship data")
    }

    override fun createOpponent(
        columns: Int,
        rows: Int,
        shipSizes: IntArray,
        random: Random
    ): /* TODO YourOpponentType*/BattleshipOpponent {
        // Note that the passing of random allows for repeatable testing
        return TODO("Create an instance of StudentBattleshipOpponent for the given game size, " +
                "target ship sizes and random generator")
    }

    override fun createGrid(
        grid: BooleanMatrix,
        opponent: BattleshipOpponent
    ): /* TODO YourGridType*/BattleshipGrid {
        // If the opponent is not a StudentBattleshipOpponent, create it based upon the passed in data
        val studentOpponent =
            opponent as? /*YourOpponentType*/BattleshipOpponent
                ?: createOpponent(opponent.columns, opponent.rows, opponent.ships.map { it as? /*YourShipType*/Ship ?: transformShip(it) })

        return TODO("Create a new grid instance with the opponent. E.g. <YourGridTypeBattleshipGrid>(studentOpponent)")
    }
}

