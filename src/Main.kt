import java.util.*

object Main {
    fun convert(value: Long): BitSet {
        var value = value
        val bits = BitSet()
        var index = 0
        while (value != 0L) {
            if (value % 2L != 0L) {
                bits.set(index)
            }
            ++index
            value = value ushr 1
        }
        return bits
    }

    fun convert(bits: BitSet): Long {
        var value = 0L
        for (i in 0 until bits.length()) {
            value += if (bits[i]) (1L shl i) else 0L
        }
        return value
    }
}
