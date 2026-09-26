package com.neoplay.radio.data.update

import com.google.gson.annotations.SerializedName

data class GithubReleaseDto(
    @SerializedName("tag_name") val tagName: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("body") val body: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    @SerializedName("assets") val assets: List<GithubAssetDto>? = null
)

data class GithubAssetDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("browser_download_url") val browserDownloadUrl: String? = null
)
