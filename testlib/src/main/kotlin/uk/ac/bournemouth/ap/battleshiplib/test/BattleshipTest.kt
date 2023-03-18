package uk.ac.bournemouth.ap.battleshiplib.test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid.Companion.DEFAULT_SHIP_SIZES
import uk.ac.bournemouth.ap.battleshiplib.test.BattleshipTest.Size
import uk.ac.bournemouth.ap.lib.matrix.boolean.BooleanMatrix
import uk.ac.bournemouth.ap.lib.matrix.boolean.MutableBooleanMatrix
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import uk.ac.bournemouth.ap.lib.matrix.ext.indices
import uk.ac.bournemouth.ap.lib.matrix.forEachIndex
import uk.ac.bournemouth.ap.lib.matrix.int.IntMatrix
import uk.ac.bournemouth.ap.lib.matrix.int.MutableIntMatrix
import java.io.InputStreamReader
import kotlin.random.Random

abstract class BattleshipTest<S : Ship> {

    /**
     * Create an opponent instance that has the given grid size and placed ships.
     * Note that the implementation of this function should not itself validate. Instead it should
     * just call the constructor
     *
     * @param columns The width of the game
     * @param rows The height of the game
     * @param ships The *placed* ships.
     */
    abstract fun createOpponent(columns: Int, rows: Int, ships: List<S>): BattleshipOpponent

    /**
     * Create a new opponent intance for the given grid size and expected ship sizes.
     *
     * @param columns The width of the game
     * @param rows The height of the game
     * @param shipSizes An array with all the ship sizes expected. There is an integer for each ship
     *   with an integer for the ship.
     * @param random The random generator to use to allow for repeatable testing.
     */
    abstract fun createOpponent(
        columns: Int,
        rows: Int,
        shipSizes: IntArray,
        random: Random
    ): BattleshipOpponent

    /**
     * Create a grid with moves for a given opponent.
     * @param grid The moves already made. Each cell already guessed has the value `true`, not guessed
     *             yet is `false`.
     * @param opponent The opponent with the ships that need to be guessed.
     */
    abstract fun createGrid(grid: BooleanMatrix, opponent: BattleshipOpponent): BattleshipGrid

    /**
     * Function that creates a specialised ship from a generic ship. If you are not using a
     * specialised `Ship` subclass you can just return the `sourceShip`
     * @param sourceShip The ship to use as basis for the specialised ship
     * @return The ship as an instance of the ship type you are looking for.
     */
    abstract fun transformShip(sourceShip: Ship): S

    fun createOpponent(size: Size, ships: List<S>): BattleshipOpponent =
        createOpponent(size.columns, size.rows, ships)

    fun createOpponent(
        size: Size,
        shipSizes: IntArray,
        random: Random
    ): BattleshipOpponent = createOpponent(size.columns, size.rows, shipSizes, random)

    fun createGrid(opponent: BattleshipOpponent): BattleshipGrid {
        return createGrid(MutableBooleanMatrix(opponent.columns, opponent.rows), opponent)
    }

    @ParameterizedTest(name = "configuration: #{index}")
    @MethodSource("validShipConfigurations")
    fun testValidShips(op: GameInfo) {
        val opponent = createOpponent(op.size, op.ships.map(::transformShip))
        assertEquals(op.ships.size, opponent.ships.size) {
            "The amount of ships created must be the same as the amount of ships passed"
        }

        op.ships.forEachIndexed { index, expectedShip ->
            assertShipEquals(expectedShip, opponent.ships[index])
        }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("randomGameData")
    fun testShipIdentity(size: Size, shipSizes: IntArray, seed: Long) {

        val grid1 = createGrid(createOpponent(size, shipSizes, Random(seed)))
        val grid2 = createGrid(createOpponent(size, shipSizes, Random(seed)))

        val opponent1a = grid1.opponent
        val opponent1b = grid1.opponent
        val opponent2 = grid2.opponent
        assertTrue(opponent1a === opponent1b) { "The grid should not make copies of the opponent." }
        assertFalse(opponent1a === opponent2) { "Different grid should not have the same opponent" }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("randomGameData")
    fun testRandomUsedCorrectly(size: Size, shipSizes: IntArray, seed: Long) {
        val rnd1 = Random(seed)
        val rnd2 = Random(seed)
        val rnd3 = Random(seed xor 0x666666L)
        val origShipSizes = shipSizes.copyOf()

        val reference = createOpponent(size, shipSizes, rnd1)
        assertArrayEquals(
            origShipSizes,
            shipSizes
        ) { "Somehow the passed in ship array was changed, this is not expected" }

        val same = createOpponent(size, shipSizes, rnd2)
        assertArrayEquals(
            origShipSizes,
            shipSizes
        ) { "Somehow the passed in ship array was changed, this is not expected" }

        val different = createOpponent(size, shipSizes, rnd3)
        assertArrayEquals(
            origShipSizes,
            shipSizes
        ) { "Somehow the passed in ship array was changed, this is not expected" }

        var differencesFound = 0
        for ((idx, refShip) in reference.ships.withIndex()) {
            val sameShip = same.ships[idx]
            val differentShip = different.ships[idx]

            assertEquals(
                origShipSizes[idx],
                refShip.size
            ) { "ships are expected to be created with the sizes requested" }
            assertEquals(
                origShipSizes[idx],
                sameShip.size
            ) { "ships are expected to be created with the sizes requested" }
            assertEquals(
                origShipSizes[idx],
                differentShip.size
            ) { "ships are expected to be created with the sizes requested" }

            assertEquals(
                refShip.left,
                sameShip.left
            ) { "With the same random seed the left coordinates are expected to be the same" }
            assertEquals(
                refShip.top,
                sameShip.top
            ) { "With the same random seed the top coordinates are expected to be the same" }
            assertEquals(
                refShip.right,
                sameShip.right
            ) { "With the same random seed the right coordinates are expected to be the same" }
            assertEquals(
                refShip.bottom,
                sameShip.bottom
            ) { "With the same random seed the bottom coordinates are expected to be the same" }

            if (refShip.left != differentShip.left || refShip.top != differentShip.top) {
                ++differencesFound
            }
        }

        val expectedMinDifferences = ((shipSizes.size * 4) / 5)
        assertTrue(differencesFound >= expectedMinDifferences) {
            "With different randoms the opponents are expected to differ for 80% of ships"
        }


    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("randomGameData")
    fun testCreateRandomOpponent(size: Size, shipSizes: IntArray, seed: Long) {
        val rnd = Random(seed)

        val totalSizes = shipSizes.sum()
        val opponent = createOpponent(size, shipSizes, rnd)
        shipSizes.forEachIndexed { index, expectedSize ->
            assertEquals(expectedSize, opponent.ships[index].size) {
                "Ship sizes must match needed ship sizes"
            }
        }

        val counts = MutableIntMatrix(size.columns, size.rows)
        for (ship in opponent.ships) {
            ship.forEachIndex { x, y ->
                assertEquals(0, counts[x, y]) {
                    "Cell [$x,$y] is part of more than one ship"
                }
                counts[x, y] += 1
            }
        }
        assertEquals(totalSizes, counts.sum()) {
            "The amount of ship cells should be the same as the sum of requested ship sizes"
        }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("shipAtData")
    fun testOpponentShipAt(opponentInfo: OpponentInfo) {
        val (size, ships, expectedGrid) = opponentInfo

        val opponent = createOpponent(size, opponentInfo.ships.map(::transformShip))

        expectedGrid.forEachIndex { x, y ->
            val actualShipIndex = opponent.shipAt(x, y)?.also { info ->
                //Ships in shipInfo should be correct
                assertShipEquals(ships[info.index], info.ship)
            }?.index ?: -1

            assertEquals(expectedGrid[x, y], actualShipIndex) {
                when {
                    actualShipIndex < 0 ->
                        "Expected ship ${expectedGrid[x, y]} but found no ship at ($x, $y)"

                    expectedGrid[x, y] < 0 ->
                        "Expected empty cell, but found ship $actualShipIndex at ($x, $y)"

                    else ->
                        "Expected cell ($x, $y) to contain ship ${expectedGrid[x, y]} but found ship $actualShipIndex"
                }

            }
        }

    }

    @ParameterizedTest(name = "configuration: #{index}")
    @MethodSource("invalidShipConfigurations")
    fun testInvalidShips(gameInfo: GameInfo) {
        val (size, ships) = gameInfo

        /* These ship configurations are invalid and should not be allowed because of for example:
         *  - overlapping ships
         *  - ships outside of the game grid
         *  - inverted dimensions (bottom < top or right < left)
         *  - Ship width not 1
         */
        val e = assertThrows<Exception> {
            val o = createOpponent(size, ships.map(::transformShip))

            System.err.println(
                ships.joinToString(
                    prefix = "Ships not detected as invalid: (",
                    postfix = ")"
                )
            )
            System.err.println(o.toMatrixString())

        }
        System.err.println("Expected: ${e.message}")
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("testGrids")
    fun testCreateGrid(size: Size, shipSizes: IntArray, seed: Long) {
        val opponent = createOpponent(size, shipSizes, Random(seed))
        val grid = createGrid(opponent)

        assertEquals(opponent.ships.size, grid.opponent.ships.size)
        opponent.ships.forEachIndexed { index, expectedShip ->
            assertShipEquals(expectedShip, grid.opponent.ships[index])
        }

        assertEquals(size.columns, grid.columns, "Column counts should match")
        assertEquals(size.rows, grid.rows, "Row counts should match")
        assertEquals(size.columns, opponent.columns, "Opponent column count should match")
        assertEquals(size.rows, opponent.rows, "Opponent column count should match")

        for ((x, y) in size.indices) {
            assertEquals(GuessCell.UNSET, grid[x, y], "Initially all cells should be unset")
        }

        assertArrayEquals(BooleanArray(shipSizes.size), grid.shipsSunk) {
            "Initially no ships should have been sunk"
        }

        assertFalse(grid.isFinished, "The game should not be finished after 1 shot")
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("shotTestData")
    fun testShootCell(opponentInfo: OpponentInfo, seed: Long) {
        val rnd = Random(seed)
        val (size, ships, expected) = opponentInfo

        val opponent = createOpponent(size, ships.map(::transformShip))
        for ((x, y) in size.indices.shuffled(rnd)) {
            // This function should be correct (tested in testOpponentShipAt)
            // This code is here to make sure that the grid is created afterwards

            val grid = createGrid(opponent)

            val actualShip = when (val shotResult = grid.shootAt(x, y)) {
                is GuessResult.HIT -> shotResult.shipIndex
                is GuessResult.SUNK -> fail("None of the ships should be length 0 so should not be sunk")
                else -> -1
            }
            assertEquals(expected[x, y], actualShip, "The ship indices must be stable")
        }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("shotTestData")
    fun testShootMisses(opponentInfo: OpponentInfo, seed: Long) {
        val (size, ships, expected) = opponentInfo
        val random = Random(seed)

        val opponent = createOpponent(size, ships.map(::transformShip))
        val shotCoordinates = expected.indices
            .filter { (x, y): Coordinate -> expected[x, y] == -1 }
            .shuffled(random)

        val grid = createGrid(opponent)

        for ((x, y) in shotCoordinates) {
            val shotResult = grid.shootAt(x, y)
            assertEquals(GuessResult.MISS, shotResult) {
                "All shots  that are not a ship for the opponent should be missed"
            }
            assertEquals(GuessCell.MISS, grid[x, y], "The recorded cell should be a miss")
        }

        for ((x, y) in size.indices) {
            when (grid[x, y]) {
                GuessCell.MISS -> { // no ship here
                    assertEquals(expected[x, y], -1) {
                        "A missed cell should not be in any ship"
                    }
                }

                GuessCell.UNSET -> {
                    assertNotEquals(expected[x, y], -1) {
                        "There must be a ship index expected for an unset (not shot at) cell"
                    }
                }

                else -> fail("Unexpected cell value, no ships should have been hit (or sunk)")
            }
        }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("shotTestData")
    fun testShootEntireShip(opponentInfo: OpponentInfo, seed: Long) {
        val (size, ships, _) = opponentInfo
        val random = Random(seed)
        val opponent = createOpponent(size, ships.map(::transformShip))

        val shipToHitIndex = ships.indices.random(random)
        val shipToHit = ships[shipToHitIndex]

        val shotCoordinates = shipToHit.rowIndices
            .flatMap { row ->
                shipToHit.columnIndices.map { col -> Coordinate(col, row) }
            }
            .shuffled(random)

        val (lastX, lastY) = shotCoordinates.last()
        val nonLastHits = shotCoordinates.dropLast(1)

        val grid = createGrid(opponent)

        // sink the preceding ships. This should not affect the test results
        // Take only returns the first n results. As indices tart with 0 this means that the index
        // is not part of that take
        for (ship in ships.take(shipToHitIndex)) {
            ship.forEachIndex { x, y -> grid.shootAt(x, y) }
        }

        nonLastHits.forEachIndexed { index, (x, y) ->
            val shotResult = grid.shootAt(x, y)
            if (shotResult !is GuessResult.HIT) fail("All shots should be hits")

            assertEquals(shipToHitIndex, shotResult.shipIndex) {
                "The ship hit should have the correct index"
            }

            // Check all ship cells for the correct value.
            shotCoordinates.forEachIndexed { i, (x2, y2) ->
                val cellValue = grid[x2, y2]
                if (i <= index) {
                    if (cellValue !is GuessCell.HIT) fail("The cell value should be a hit")

                    assertEquals(shipToHitIndex, cellValue.shipIndex) {
                        "The recorded cell should have the correct ship index"
                    }
                } else {
                    assertEquals(GuessCell.UNSET, cellValue) {
                        "When not shot at yet the cell value should be unset"
                    }
                }
            }
        }

        val shotResult = grid.shootAt(lastX, lastY)
        if (shotResult !is GuessResult.SUNK) fail("Shooting the last cell should sink the ship")

        shipToHit.forEachIndex { x, y ->
            val cell = grid[x, y]
            if (cell !is GuessCell.SUNK) fail("After sinking a shipall cells must be sunk")
            assertEquals(shipToHitIndex, cell.shipIndex) {
                "The index of the sunk ship should be correct"
            }
        }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("maxFullGames")
    fun testPlayMaxFullGame(gameInfo: GameInfo) {
        val (size, ships, moves) = gameInfo
        val opponent = createOpponent(size, ships.map(::transformShip))

        val lastShipCoordinate = moves.last()

        val nonLastMoves = moves.dropLast(1)

        val unsunkShipIndex = ships
            .mapIndexed { idx, s -> if (lastShipCoordinate in s) idx else -1 }
            .single { it >= 0 }
        val unsunkShip = ships[unsunkShipIndex]


        val grid = createGrid(opponent)

        val shipHitCounts = IntArray(grid.opponent.ships.size)
        // Check all moves except the last
        assertMovesValid(grid, nonLastMoves) { c, r ->
            assertFalse(grid.isFinished) {
                "As one ship cell is kept for last the game should not be finished"
            }
            when (r) {
                is GuessResult.HIT -> shipHitCounts[r.shipIndex]++
                is GuessResult.SUNK -> shipHitCounts[r.shipIndex]++
                is GuessResult.MISS -> { /* Ignore misses */
                }
            }
        }

        assertGridCells(grid) { c ->
            val shipInfo = opponent.shipAt(c)
            when {
                c == lastShipCoordinate -> GuessCell.UNSET
                shipInfo == null -> GuessCell.MISS
                shipInfo.ship.size == shipHitCounts[shipInfo.index]
                -> GuessCell.SUNK(shipInfo.index)

                else ->
                    GuessCell.HIT(shipInfo.index)
            }
        }

        assertMoveValid(GuessResult.SUNK(unsunkShipIndex), grid, lastShipCoordinate)

        assertEquals(unsunkShip.size - 1, shipHitCounts[unsunkShipIndex]) {
            "The counted hits on the last ship should be 1 less than the size"
        }

        assertTrue(grid.isFinished, "The game should be finished")

    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("normalGames")
    fun testPlayRegularGame(gameInfo: GameInfo) {
        val (size, ships, moves) = gameInfo
        val opponent = createOpponent(size, ships.map(::transformShip))

        val lastShipCoordinate = moves.last()

        val nonLastMoves = moves.dropLast(1)

        val unsunkShipIndex = ships
            .mapIndexed { idx, s -> if (lastShipCoordinate in s) idx else -1 }
            .single { it >= 0 }
        val unsunkShip = ships[unsunkShipIndex]

        val grid = createGrid(opponent)

        val shipHitCounts = IntArray(grid.opponent.ships.size)
        // Check all moves except the last
        assertMovesValid(grid, nonLastMoves) { c, r ->
            assertFalse(grid.isFinished) {
                "As one ship cell is kept for last the game should not be finished"
            }
            when (r) {
                is GuessResult.HIT -> shipHitCounts[r.shipIndex]++
                is GuessResult.SUNK -> shipHitCounts[r.shipIndex]++
                is GuessResult.MISS -> { /* Ignore misses */
                }
            }
        }

        assertGridCells(grid) { c ->
            val shipInfo = opponent.shipAt(c)
            when {
                c !in nonLastMoves -> GuessCell.UNSET
                shipInfo == null -> GuessCell.MISS
                shipInfo.ship.size == shipHitCounts[shipInfo.index]
                -> GuessCell.SUNK(shipInfo.index)

                else ->
                    GuessCell.HIT(shipInfo.index)
            }
        }

        assertMoveValid(GuessResult.SUNK(unsunkShipIndex), grid, lastShipCoordinate)

        assertEquals(unsunkShip.size - 1, shipHitCounts[unsunkShipIndex]) {
            "The counted hits on the last ship should be 1 less than the size"
        }

        assertTrue(grid.isFinished, "The game should be finished")

    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("normalGames")
    fun testListeners(gameInfo: GameInfo) {
        val (size, ships, moves) = gameInfo
        val opponent = createOpponent(size, ships.map(::transformShip))

        val lastShipCoordinate = moves.last()

        val unsunkShipIndex = opponent.shipAt(lastShipCoordinate)!!.index

        val grid = createGrid(opponent)
        val testListener = TestListener(grid)

        grid.addOnGridChangeListener(testListener)

        var expectedInvocationCount = 0
        // Check all moves except the last
        assertMovesValid(grid, moves.dropLast(1)) { c, r ->
            expectedInvocationCount++
            assertEquals(expectedInvocationCount, testListener.invocationCount) {
                "change listener should be invoked once for this move"
            }

            assertEquals(c, testListener.lastChanged) {
                "The coordinate passed to the listener should be the coordinate of the move"
            }

            assertFalse(
                testListener.isFinished,
                "The game should not be finished before the last move"
            )
        }

        assertMoveValid(GuessResult.SUNK(unsunkShipIndex), grid, lastShipCoordinate)
        assertTrue(testListener.invocationCount > expectedInvocationCount) {
            "The invocation count of the listener must be increased after the last move. For the last move this may be invoked more than once"
        }
        assertTrue(testListener.isFinished, "The game should be finished")

        assertTrue(grid.isFinished, "The game should be finished")

    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("gamesWithRnd")
    fun testDisallowDuplicates(gameInfo: GameInfo, rnd: Random) {
        val grid = createGrid(createOpponent(gameInfo.size, gameInfo.ships.map(::transformShip)))

        val seenCoordinates = mutableListOf<Coordinate>()
        for (c in gameInfo.moves) {
            grid.shootAt(c)
            seenCoordinates.add(c)
            assertThrows<Exception>("Duplicate moves should throw an exception") {
                val duplicateCoordinate = seenCoordinates.random(rnd)
                grid.shootAt(duplicateCoordinate)
            }
        }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("outOfBoundsGames")
    fun testDisallowOutOfBounds(gameInfo: GameInfo) {
        val grid = createGrid(createOpponent(gameInfo.size, gameInfo.ships.map(::transformShip)))

        for (c in gameInfo.moves) {
            assertThrows<Exception>("Out of bounds moves should throw an exception") {
                grid.shootAt(c)
            }
        }
    }

    @ParameterizedTest(name = "configuration: ({0}) #{2}")
    @MethodSource("minFullGames")
    fun testPlayMinFullGame(opponentInfo: GameInfo) {
        val (size, ships, moves) = opponentInfo
        val opponent = createOpponent(size, ships.map(::transformShip))

        val lastShipCoordinate = moves.last()
        val unsunkShipIndex = ships
            .mapIndexed { idx, s -> if (lastShipCoordinate in s) idx else -1 }
            .single { it >= 0 }
        val unsunkShip = opponent.ships[unsunkShipIndex]

        val shipHitCounts = IntArray(ships.size)


        val grid = createGrid(opponent)

        assertMovesValid(grid, moves.asSequence().take(moves.size - 1)) { c, it ->
            assertFalse(it is GuessResult.MISS) {
                "For a minimum game no miss results are valid ($c)\n${
                    grid.opponent.toMatrixString().prependIndent("    ")
                }"
            }
            assertFalse(grid.isFinished) {
                "As one ship cell is kept for last the game should not be finished"
            }
            when (it) {
                is GuessResult.HIT -> shipHitCounts[it.shipIndex]++
                is GuessResult.SUNK -> shipHitCounts[it.shipIndex]++
                is GuessResult.MISS -> { /* Ignore misses */
                }
            }
        }

        val lastResult = grid.shootAt(lastShipCoordinate)

        if (lastResult !is GuessResult.SUNK) fail("The last action should mean the ship has been sunk")

        assertEquals(
            unsunkShipIndex,
            lastResult.shipIndex,
            "The sunk ship should have the correct index"
        )

        unsunkShip.forEachIndex { x, y ->
            val cell = grid[x, y]
            if (cell !is GuessCell.SUNK) fail("After shooting the last ship's cells should be sunk")
            assertEquals(
                unsunkShipIndex,
                cell.shipIndex,
                "The ship index of the now sunk cells should be correct"
            )
        }
        assertEquals(
            ships[unsunkShipIndex].size - 1, shipHitCounts[unsunkShipIndex],
            "The counted hits on the last ship should be 1 less than the size"
        )

        assertTrue(grid.isFinished, "The game should be finished")

    }

    class TestListener(private val expectedGrid: BattleshipGrid) : BattleshipGrid.BattleshipGridListener {
        var isFinished: Boolean = false
            private set
        var invocationCount = 0
            private set
        var lastChanged = Coordinate(-1, -1)
            private set

        override fun onGridChanged(grid: BattleshipGrid, column: Int, row: Int) {
            invocationCount++
            lastChanged = Coordinate(column, row)

            assertEquals(expectedGrid, grid, "The listener should return the actual grid")
            if (grid.isFinished) {
                assertFalse(isFinished, "The game finished state should only be notified once")
                isFinished = true
            }
        }
    }


    data class Size(val columns: Int, val rows: Int) {
        constructor(size: Int) : this(size, size)

        val rowIndices: IntRange get() = 0 until rows
        val colIndices: IntRange get() = 0 until columns
        val indices get() = rowIndices.flatMap { y -> colIndices.map { x -> Coordinate(x, y) } }
    }

    private data class TestShip(
        override val top: Int,
        override val left: Int,
        override val bottom: Int,
        override val right: Int
    ) : Ship {
        constructor(size: Int, isHorizontal: Boolean, top: Int, left: Int) :
                this(
                    top,
                    left,
                    if (isHorizontal) top else top + size - 1,
                    if (isHorizontal) left + size - 1 else left
                )
    }

    data class OpponentInfo(val size: Size, val ships: List<Ship>, val matrix: IntMatrix)

    data class GameInfo(
        val size: Size,
        val ships: List<Ship>,
        val moves: List<Coordinate> = emptyList()
    )

    companion object {

        fun readGameInfo(resourceName: String): List<GameInfo> {
            return BattleshipTest::class.java.classLoader.getResourceAsStream(resourceName)!!
                .use { inStream ->
                    InputStreamReader(inStream).useLines { lines ->
                        lines
                            .filterNot { it.isEmpty() }
                            .map { line ->
                                val elems = line.splitToSequence(',').map { it.toInt() }.iterator()
                                val size = Size(elems.next(), elems.next())
                                val ships = (0 until elems.next()).map {
                                    TestShip(elems.next(), elems.next(), elems.next(), elems.next())
                                }
                                val moves = mutableListOf<Coordinate>()
                                while (elems.hasNext()) {
                                    moves.add(Coordinate(elems.next(), elems.next()))
                                }
                                GameInfo(size, ships, moves)
                            }
                            .toList()
                    }
                }

        }

        fun readOpponentInfo(resourceName: String): List<OpponentInfo> {
            return BattleshipTest::class.java.classLoader.getResourceAsStream(resourceName)!!
                .use { inStream ->
                    InputStreamReader(inStream).useLines { lines ->
                        lines
                            .filterNot { it.isEmpty() }
                            .map { line ->
                                val elems =
                                    line.split/*ToSequence*/(',').map { it.toInt() }.iterator()
                                val size = Size(elems.next(), elems.next())
                                val rawData = IntArray(size.columns * size.rows) { _ -> elems.next() }
                                val shipData =
                                    IntMatrix(size.columns, size.rows) { x, y -> rawData[x + y*size.columns] }
                                val ships = mutableListOf<Ship>()
                                while (elems.hasNext()) {
                                    ships.add(
                                        TestShip(
                                            top = elems.next(),
                                            left = elems.next(),
                                            bottom = elems.next(),
                                            right = elems.next()
                                        )
                                    )
                                }
                                OpponentInfo(size, ships, shipData)
                            }
                            .toList()
                    }
                }

        }

        @JvmStatic
        fun validShipConfigurations() = readGameInfo("validShipInfo.txt")

        @JvmStatic
        fun invalidShipConfigurations() = readGameInfo("invalidShipInfo.txt")

        @JvmStatic
        fun minFullGames() = readGameInfo("minFullGames.txt")

        @JvmStatic
        fun maxFullGames() = readGameInfo("maxFullGames.txt")

        @JvmStatic
        fun normalGames() = readGameInfo("randomGames.txt") + readGameInfo("computerGames.txt")

        @JvmStatic
        fun outOfBoundsGames() = readGameInfo("outOfBoundsMoves.txt")

        @JvmStatic
        fun gamesWithRnd() = Random(7523).let { rnd ->
            (minFullGames() + maxFullGames() + normalGames()).map {
                arrayOf(it, Random(rnd.nextLong()))
            }
        }

        @JvmStatic
        fun randomGameData(): List<Array<Any>> {
            return listOf(
                arrayOf(Size(10, 10), DEFAULT_SHIP_SIZES, 1324L),
                arrayOf(Size(10, 10), DEFAULT_SHIP_SIZES, 56798941L),
                arrayOf(Size(5, 5), DEFAULT_SHIP_SIZES, 3248L),
                arrayOf(Size(10, 3), DEFAULT_SHIP_SIZES, 1534L),
                arrayOf(Size(3, 10), DEFAULT_SHIP_SIZES, 1534L),
            )
        }

        @JvmStatic
        fun testGrids(): List<Array<Any>> {
            return listOf(
                arrayOf(Size(10, 10), DEFAULT_SHIP_SIZES, 13242L),
                arrayOf(Size(10, 10), DEFAULT_SHIP_SIZES, 5679894231L),
                arrayOf(Size(5, 5), DEFAULT_SHIP_SIZES, 32448L),
                arrayOf(Size(10, 3), DEFAULT_SHIP_SIZES, 15634L),
                arrayOf(Size(3, 10), DEFAULT_SHIP_SIZES, 15034L),
            )
        }

        @JvmStatic
        fun shipAtData() = readOpponentInfo("shipAt.txt")

        @JvmStatic
        fun shotTestData() =
            Random(19085).let { r -> shipAtData().map { arrayOf(it, r.nextLong()) } }

    }
}

fun List<Ship>.counts(columns: Int, rows: Int): IntMatrix {
    return MutableIntMatrix(columns, rows).also { result ->
        for (ship in this) {
            ship.forEachIndex { x, y -> result[x, y] += 1 }
        }
    }
}

operator fun Ship.contains(coordinate: Coordinate): Boolean {
    return coordinate.x in columnIndices && coordinate.y in rowIndices
}

val Ship.coordinates: List<Coordinate>
    get() = columnIndices.flatMap { x ->
        rowIndices.map { y ->
            Coordinate(x, y)
        }
    }

fun BattleshipOpponent.toMatrixString() =
    (0 until rows).joinToString("\n") { y ->
        (0 until columns).joinToString(" ", "    ") { x ->
            shipAt(x, y)?.index?.toString() ?: "."
        }
    }

fun assertShipEquals(expected: Ship, actual: Ship) {
    assertEquals(
        expected.left,
        actual.left,
        "Ships are expected to match (left coordinate)"
    )
    assertEquals(
        expected.top,
        actual.top,
        "Ships are expected to match (top coordinate)"
    )
    assertEquals(
        expected.right,
        actual.right,
        "Ships are expected to match (right coordinate)"
    )
    assertEquals(
        expected.bottom,
        actual.bottom,
        "Ships are expected to match (bottom coordinate)"
    )
    assertEquals(expected.width, actual.width, "Width should match")
    assertEquals(expected.height, actual.height, "Height should match")
    assertEquals(expected.size, actual.size, "Sizes should match")
    assertEquals(expected.columnIndices, actual.columnIndices, "Column indices should match")
    assertEquals(expected.rowIndices, actual.rowIndices, "Row indices should match")
}

inline fun assertGridCells(grid: BattleshipGrid, expectedCellFun: (Coordinate) -> GuessCell?) {
    for (c in Size(grid.columns, grid.rows).indices) {
        val expectedCell = expectedCellFun(c)
        if (expectedCell != null) {
            assertEquals(expectedCell, grid[c.x, c.y], "The grid cells need to match expectations")
        }
    }
}

inline fun assertMovesValid(
    grid: BattleshipGrid,
    moves: Iterable<Coordinate>,
    check: (Coordinate, GuessResult) -> Unit = { _, _ -> }
) = assertMovesValid(grid, moves.asSequence(), check)

inline fun assertMovesValid(
    grid: BattleshipGrid,
    moves: Sequence<Coordinate>,
    check: (Coordinate, GuessResult) -> Unit = { _, _ -> }
) {
    val shipHitCounts = IntArray(grid.opponent.ships.size)
    // Check all moves
    for (c in moves) {
        val (x, y) = c
        val expectedResult = when (val opponentCell = grid.opponent.shipAt(c)) {
            null -> GuessResult.MISS
            else -> opponentCell.index.let { shipIdx ->
                shipHitCounts[shipIdx]++
                when (shipHitCounts[shipIdx]) {
                    grid.opponent.ships[shipIdx].size -> GuessResult.SUNK(shipIdx)
                    else -> GuessResult.HIT(shipIdx)
                }
            }
        }
        val actualResult = assertMoveValid(expectedResult, grid, c)

        check(c, actualResult)
    }

}

fun assertMoveValid(
    expectedResult: GuessResult,
    grid: BattleshipGrid,
    c: Coordinate,
): GuessResult {
    val opponentCell = grid.opponent.shipAt(c)

    assertEquals(GuessCell.UNSET, grid[c], "Before shooting the cell should be unset")

    val actualResult = grid.shootAt(c)
    assertEquals(expectedResult, actualResult) {
        "The expected and actual result should be the same"
    }

    when (actualResult) {
        is GuessResult.HIT -> {
            val ship = grid.opponent.ships[actualResult.shipIndex]
            val hitCount = ship.coordinates.count {
                when (grid[it.x, it.y]) {
                    is GuessCell.SUNK -> fail("None of the cells should be sunk on a hit result")
                    is GuessCell.MISS -> fail("None of the cells of a ship should be a miss")
                    is GuessCell.HIT -> true
                    GuessCell.UNSET -> false
                }
            }

            if (opponentCell == null) fail("There should be a ship corresponding to the hit cell")
            assertEquals(opponentCell.index, actualResult.shipIndex) {
                "The hit ship should be the expected ship"
            }
            assertTrue(hitCount < grid.opponent.ships[actualResult.shipIndex].size) {
                "For the result to be hit the ship should not have been hit to full size yet"
            }
        }

        is GuessResult.SUNK -> {
            if (opponentCell == null) fail("There should be a ship corresponding to the hit cell")

            assertEquals(opponentCell.index, actualResult.shipIndex) {
                "The hit ship should be the expected ship"
            }

            val ship = grid.opponent.ships[actualResult.shipIndex]
            val hitCount = ship.coordinates.count {
                when (grid[it.x, it.y]) {
                    is GuessCell.HIT -> fail("None of the cells should be hit on a SUNK result")
                    is GuessCell.MISS -> fail("None of the cells of a ship should be a miss")
                    is GuessCell.SUNK -> true
                    GuessCell.UNSET -> fail("None of the cells should be unset on a sunk result")
                }
            }

            assertEquals(hitCount, grid.opponent.ships[actualResult.shipIndex].size) {
                "For the result to be sunk the ship should not have been exactly the expected times"
            }

        }

        is GuessResult.MISS -> { /* Ignore misses */
        }
    }
    return actualResult
}

inline fun <reified T> assertInstance(value: Any?, message: String) {
    if (value !is T) fail(message)
}
