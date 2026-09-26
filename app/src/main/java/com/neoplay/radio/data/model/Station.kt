package com.neoplay.radio.data.model

import java.io.Serializable

data class Station(
    val stationuuid: String,
    val name: String,
    val url: String,
    val urlResolved: String,
    val homepage: String,
    val favicon: String,
    val tags: String,
    val country: String,
    val countrycode: String,
    val state: String,
    val language: String,
    val votes: Int,
    val codec: String,
    val bitrate: Int,
    val isFavorite: Boolean = false
) : Serializable {
    val displayTitle: String
        get() = name.ifBlank { "Radio Station" }

    val formattedBitrate: String
        get() = if (bitrate > 0) "${bitrate} kbps" else "Unknown"
}
