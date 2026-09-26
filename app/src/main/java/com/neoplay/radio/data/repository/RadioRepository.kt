package com.neoplay.radio.data.repository

import com.neoplay.radio.data.api.ApiClient
import com.neoplay.radio.data.api.RadioApi
import com.neoplay.radio.data.api.dto.StationDto
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.data.preferences.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadioRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    private var cachedStations: List<Station> = emptyList()

    fun getCachedStations(): List<Station> = cachedStations

    private val publicRadioBrowserApi: RadioApi by lazy {
        ApiClient.createRadioApi("https://de1.api.radio-browser.info/")
    }

    private suspend fun getCustomApi(): RadioApi {
        val baseUrl = settingsDataStore.apiBaseUrl.first()
        return ApiClient.createRadioApi(baseUrl)
    }

    fun getStationsFlow(country: String? = null, genre: String? = null): Flow<List<Station>> = flow {
        var dtos: List<StationDto> = emptyList()

        // 1. Try Custom Backend
        try {
            val customApi = getCustomApi()
            dtos = customApi.getBackendStations(country = country, genre = genre, limit = 300)
        } catch (e: Exception) {
            dtos = emptyList()
        }

        // 2. Fallback to Public RadioBrowser API if Custom Backend fails or returns empty
        if (dtos.isEmpty()) {
            try {
                val c = country
                val g = genre
                dtos = when {
                    !c.isNullOrBlank() -> {
                        val countryCode = getCountryCode(c)
                        val listByCode = if (countryCode != null) {
                            publicRadioBrowserApi.getStationsByCountryCode(countryCode, limit = 300)
                        } else emptyList()

                        if (listByCode.isNotEmpty()) {
                            listByCode
                        } else {
                            publicRadioBrowserApi.getStationsByCountry(c, limit = 300)
                        }
                    }
                    !g.isNullOrBlank() -> publicRadioBrowserApi.getStationsByTag(g.lowercase(), limit = 300)
                    else -> {
                        val topVote = publicRadioBrowserApi.getTopVotedStations(limit = 200)
                        val topClick = publicRadioBrowserApi.getTopClickStations(limit = 200)
                        (topVote + topClick).distinctBy { it.stationuuid }
                    }
                }

                // Secondary fallback by country name if code returned empty
                if (dtos.isEmpty() && !c.isNullOrBlank()) {
                    dtos = publicRadioBrowserApi.getStationsByCountry(c, limit = 300)
                }
            } catch (e: Exception) {
                dtos = emptyList()
            }
        }

        val stations = dtos.map { it.toDomainModel() }
        cachedStations = stations
        emit(stations)
    }.combine(settingsDataStore.favoriteUuids) { stations, favs ->
        stations.map { s -> s.copy(isFavorite = favs.contains(s.stationuuid)) }
    }

    suspend fun searchStations(query: String): List<Station> {
        val favs = settingsDataStore.favoriteUuids.first()
        var dtos: List<StationDto> = emptyList()

        try {
            val customApi = getCustomApi()
            dtos = customApi.searchBackendStations(query = query, limit = 100)
        } catch (e: Exception) {
            dtos = emptyList()
        }

        if (dtos.isEmpty()) {
            try {
                dtos = publicRadioBrowserApi.searchRadioBrowserStations(name = query, limit = 100)
            } catch (e: Exception) {
                dtos = emptyList()
            }
        }

        return dtos.map { it.toDomainModel().copy(isFavorite = favs.contains(it.stationuuid)) }
    }

    fun getFavoriteStations(): Flow<List<Station>> = combine(
        getStationsFlow(),
        settingsDataStore.favoriteUuids
    ) { stations, favs ->
        stations.filter { favs.contains(it.stationuuid) }
    }

    suspend fun toggleFavorite(uuid: String) {
        settingsDataStore.toggleFavorite(uuid)
    }

    private fun getCountryCode(countryName: String): String? {
        return when (countryName.lowercase().trim()) {
            "azerbaijan", "az", "azərbaycan" -> "AZ"
            "turkey", "türkiye", "tr", "turkiye" -> "TR"
            "russia", "ru", "россия" -> "RU"
            "germany", "de", "deutschland" -> "DE"
            "united states", "usa", "us", "america" -> "US"
            "united kingdom", "uk", "gb", "england" -> "GB"
            "france", "fr" -> "FR"
            "spain", "es" -> "ES"
            "italy", "it" -> "IT"
            "canada", "ca" -> "CA"
            "australia", "au" -> "AU"
            "brazil", "br" -> "BR"
            "japan", "jp" -> "JP"
            "south korea", "kr" -> "KR"
            "china", "cn" -> "CN"
            "india", "in" -> "IN"
            "netherlands", "nl" -> "NL"
            "sweden", "se" -> "SE"
            "switzerland", "ch" -> "CH"
            "norway", "no" -> "NO"
            "poland", "pl" -> "PL"
            "ukraine", "ua" -> "UA"
            "georgia", "ge" -> "GE"
            "kazakhstan", "kz" -> "KZ"
            "uzbekistan", "uz" -> "UZ"
            "united arab emirates", "ae", "dubai" -> "AE"
            "saudi arabia", "sa" -> "SA"
            "mexico", "mx" -> "MX"
            "argentina", "ar" -> "AR"
            "greece", "gr" -> "GR"
            else -> null
        }
    }

    private fun StationDto.toDomainModel(): Station {
        return Station(
            stationuuid = this.stationuuid.orEmpty(),
            name = this.name.orEmpty(),
            url = this.url.orEmpty(),
            urlResolved = this.urlResolved.orEmpty().ifBlank { this.url.orEmpty() },
            homepage = this.homepage.orEmpty(),
            favicon = this.favicon.orEmpty(),
            tags = this.tags.orEmpty(),
            country = this.country.orEmpty(),
            countrycode = this.countrycode.orEmpty(),
            state = this.state.orEmpty(),
            language = this.language.orEmpty(),
            votes = this.votes ?: 0,
            codec = this.codec.orEmpty(),
            bitrate = this.bitrate ?: 0
        )
    }
}
