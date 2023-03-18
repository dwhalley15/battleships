package uk.ac.bournemouth.ap.battleshiplib

sealed class GuessResult {
    object MISS : GuessResult() {
        override fun toString() = "MISS"
    }
    data class HIT(val shipIndex:Int) : GuessResult()
    data class SUNK(val shipIndex: Int) : GuessResult()
}