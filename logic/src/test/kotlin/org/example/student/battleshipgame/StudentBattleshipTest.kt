package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.battleshiplib.test.BattleshipTest
import uk.ac.bournemouth.ap.battleshipslogic.*
import uk.ac.bournemouth.ap.lib.matrix.boolean.BooleanMatrix
import kotlin.random.Random

class StudentBattleshipTest : BattleshipTest<MyShip>() {
    override fun createOpponent(
        columns: Int,
        rows: Int,
        ships: List<MyShip>
    ): MySecondOpponent {
        return MySecondOpponent(columns, rows, ships)
    }

    override fun transformShip(sourceShip: Ship): MyShip {
        return MyShip(sourceShip.top, sourceShip.left, sourceShip.bottom, sourceShip.right)
    }

    override fun createOpponent(
        columns: Int,
        rows: Int,
        shipSizes: IntArray,
        random: Random
    ): MyOpponent {
        // Note that the passing of random allows for repeatable testing
        return MyOpponent(columns, rows, shipSizes, random)
    }

    override fun createGrid(
        grid: BooleanMatrix,
        opponent: BattleshipOpponent
    ): MySecondGrid {
        // If the opponent is not a StudentBattleshipOpponent, create it based upon the passed in data
        val studentOpponent =
            opponent as? MySecondOpponent
                ?: createOpponent(opponent.columns, opponent.rows, opponent.ships.map { it as? MyShip ?: transformShip(it) })

        return MySecondGrid(opponent.columns, opponent.rows, studentOpponent)
    }
}

