package com.neoplay.radio.data.update

import retrofit2.http.GET

interface GithubApi {
    @GET("repos/BY-KERIMOFF/tunex/releases/latest")
    suspend fun getLatestRelease(): GithubReleaseDto

    @GET("repos/BY-KERIMOFF/tunex/tags")
    suspend fun getTags(): List<GithubTagDto>
}
