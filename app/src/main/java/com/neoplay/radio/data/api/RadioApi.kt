package com.neoplay.radio.data.api

import com.neoplay.radio.data.api.dto.StationDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadioApi {

    @GET("stations")
    suspend fun getBackendStations(
        @Query("country") country: String? = null,
        @Query("genre") genre: String? = null,
        @Query("limit") limit: Int = 300
    ): List<StationDto>

    @GET("search")
    suspend fun searchBackendStations(
        @Query("q") query: String,
        @Query("limit") limit: Int = 100
    ): List<StationDto>

    // Public RadioBrowser Endpoints
    @GET("json/stations/topvote/{limit}")
    suspend fun getTopVotedStations(
        @Path("limit") limit: Int = 300
    ): List<StationDto>

    @GET("json/stations/topclick/{limit}")
    suspend fun getTopClickStations(
        @Path("limit") limit: Int = 300
    ): List<StationDto>

    @GET("json/stations/bycountry/{country}")
    suspend fun getStationsByCountry(
        @Path("country") country: String,
        @Query("limit") limit: Int = 300
    ): List<StationDto>

    @GET("json/stations/bycountrycodeexact/{code}")
    suspend fun getStationsByCountryCode(
        @Path("code") code: String,
        @Query("limit") limit: Int = 300
    ): List<StationDto>

    @GET("json/stations/bytag/{tag}")
    suspend fun getStationsByTag(
        @Path("tag") tag: String,
        @Query("limit") limit: Int = 300
    ): List<StationDto>

    @GET("json/stations/search")
    suspend fun searchRadioBrowserStations(
        @Query("name") name: String? = null,
        @Query("country") country: String? = null,
        @Query("tag") tag: String? = null,
        @Query("limit") limit: Int = 200,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true
    ): List<StationDto>
}
