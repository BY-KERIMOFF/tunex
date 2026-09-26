package com.neoplay.radio

import com.neoplay.radio.player.PlayerStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerViewModelTest {

    @Test
    fun testPlayerStatuses() {
        val statuses = PlayerStatus.values()
        assertEquals(5, statuses.size)
        assertEquals(PlayerStatus.IDLE, PlayerStatus.valueOf("IDLE"))
        assertEquals(PlayerStatus.PLAYING, PlayerStatus.valueOf("PLAYING"))
    }
}
