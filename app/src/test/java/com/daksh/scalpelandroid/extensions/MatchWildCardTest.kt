package com.daksh.scalpelandroid.extensions

import org.junit.Test

class MatchWildCardTest {

    @Test
    fun matchWithWildCard() {
        var actual = "abc".toScalpelBytes()
        var withWildCard = "a?c".toScalpelBytes()
        assert(actual.matchWithWildCard(withWildCard))

        actual = "\\xff\\xd9\\xd81".toScalpelBytes()
        withWildCard = "\\xff\\xd9??".toScalpelBytes()
        assert(actual.matchWithWildCard(withWildCard))
    }
}
