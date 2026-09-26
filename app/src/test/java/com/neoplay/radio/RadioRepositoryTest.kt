package com.neoplay.radio

import com.neoplay.radio.data.model.Station
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RadioRepositoryTest {

    @Test
    fun testStationFormatting() {
        val station = Station(
            stationuuid = "uuid-123",
            name = "Radio Baku",
            url = "http://stream.radio.az/live",
            urlResolved = "http://stream.radio.az/live",
            homepage = "http://radio.az",
            favicon = "http://radio.az/logo.png",
            tags = "pop,news",
            country = "Azerbaijan",
            countrycode = "AZ",
            state = "Baku",
            language = "azerbaijani",
            votes = 100,
            codec = "MP3",
            bitrate = 128
        )

        assertEquals("Radio Baku", station.displayTitle)
        assertEquals("128 kbps", station.formattedBitrate)
        assertTrue(station.country == "Azerbaijan")
    }

    @Test
    fun testStationFallbackTitle() {
        val station = Station(
            stationuuid = "uuid-456",
            name = "",
            url = "http://stream.test.com",
            urlResolved = "http://stream.test.com",
            homepage = "",
            favicon = "",
            tags = "",
            country = "",
            countrycode = "",
            state = "",
            language = "",
            votes = 0,
            codec = "",
            bitrate = 0
        )

        assertEquals("Radio Station", station.displayTitle)
        assertEquals("Unknown", station.formattedBitrate)
    }
}
