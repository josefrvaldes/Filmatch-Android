package es.josevaldes.filmatch

import org.junit.Test

open class Mierda(val a: Int, val b: String) {
    override fun toString(): String {
        return "Mierda(a=$a, b='$b')"
    }
}


class MierdaDeluxe(a: Int, b: String, val c: Boolean) : Mierda(a, b) {
    override fun toString(): String {
        return "MierdaDeluxe(a=$a, b='$b', c=$c)"
    }
}

class JustTest {

    @Test
    fun testingDataClasses() {
        val mierda = Mierda(1, "mierda normal")
        val mierdaDeluxe = MierdaDeluxe(1, "mierda deluxe", true)
        println(mierda)
        println(mierdaDeluxe)
    }
}