package uk.ac.bournemouth.ap.battleshiplib

sealed class GuessCell {
    object UNSET : GuessCell() {
        override fun toByte(): Byte = -128
        override fun toString() = "UNSET"
    }

    object MISS : GuessCell() {
        override fun toByte(): Byte = -127
        override fun toString() = "MISS"
    }

    data class HIT(val shipIndex: Int) : GuessCell() {
        init {
            if (shipIndex !in 0..126)
                throw IllegalArgumentException("Ship indices must be in the range 0..126, was: $shipIndex")
        }
        override fun toByte(): Byte = shipIndex.toByte()
    }

    data class SUNK(val shipIndex: Int) : GuessCell() {
        init {
            if (shipIndex !in 0..126)
                throw IllegalArgumentException("Ship indices must be in the range 0..126, was: $shipIndex")
        }
        override fun toByte(): Byte = (-1 - shipIndex).toByte()
    }

    abstract fun toByte(): Byte

    companion object {
        fun fromByte(byte: Byte): GuessCell = when (byte.toInt()) {
            -128 -> UNSET
            -127 -> MISS
            else -> if (byte >= 0) HIT(byte.toInt()) else SUNK(-1 - byte)
        }
    }

}