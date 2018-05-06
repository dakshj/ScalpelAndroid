package com.daksh.scalpelandroid.extensions

import org.junit.Test

class StringToScalpelBytesTest {

    @Test
    fun toScalpelBytes() {
        assert("\\xFF".toScalpelBytes()[0].toUnsignedInt() == 255)
        assert("\\xff".toScalpelBytes()[0].toUnsignedInt() == 255)

        assert("a\\xff".toScalpelBytes()[0].toUnsignedInt() == 97)
        assert("a\\xff".toScalpelBytes()[1].toUnsignedInt() == 255)

        assert("a\\xffc".toScalpelBytes()[0].toUnsignedInt() == 97)
        assert("a\\xffc".toScalpelBytes()[1].toUnsignedInt() == 255)
        assert("a\\xffc".toScalpelBytes()[2].toUnsignedInt() == 99)
    }
}
