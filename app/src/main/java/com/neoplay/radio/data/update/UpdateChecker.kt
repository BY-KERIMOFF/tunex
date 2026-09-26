package com.neoplay.radio.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.neoplay.radio.BuildConfig
import com.neoplay.radio.data.api.ApiClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class UpdateInfo(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val currentVersion: String = BuildConfig.VERSION_NAME,
    val downloadUrl: String,
    val releaseNotes: String
)

@Singleton
class UpdateChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val githubApi: GithubApi by lazy {
        ApiClient.createRetrofit("https://api.github.com/").create(GithubApi::class.java)
    }

    suspend fun checkForUpdates(): UpdateInfo = withContext(Dispatchers.IO) {
        val currentVer = BuildConfig.VERSION_NAME.trim()

        // 1. Try Releases API
        try {
            val release = githubApi.getLatestRelease()
            val rawTag = release.tagName.orEmpty().removePrefix("v").trim()
            if (rawTag.isNotBlank()) {
                val hasUpdate = isVersionNewer(currentVer, rawTag)
                val downloadUrl = release.assets?.firstOrNull { it.name?.endsWith(".apk") == true }?.browserDownloadUrl
                    ?: release.htmlUrl
                    ?: "https://github.com/BY-KERIMOFF/tunex/releases"

                return@withContext UpdateInfo(
                    hasUpdate = hasUpdate,
                    latestVersion = rawTag,
                    currentVersion = currentVer,
                    downloadUrl = downloadUrl,
                    releaseNotes = release.body.orEmpty().ifBlank { "Yeni yenilənmə mövcuddur!" }
                )
            }
        } catch (_: Exception) {}

        // 2. Fallback to Tags API
        try {
            val tags = githubApi.getTags()
            val latestTag = tags.firstOrNull()?.name?.removePrefix("v")?.trim().orEmpty()
            if (latestTag.isNotBlank()) {
                val hasUpdate = isVersionNewer(currentVer, latestTag)
                return@withContext UpdateInfo(
                    hasUpdate = hasUpdate,
                    latestVersion = latestTag,
                    currentVersion = currentVer,
                    downloadUrl = "https://github.com/BY-KERIMOFF/tunex/releases",
                    releaseNotes = "Yeni versiya mövcuddur! (v$latestTag)"
                )
            }
        } catch (_: Exception) {}

        UpdateInfo(
            hasUpdate = false,
            latestVersion = currentVer,
            currentVersion = currentVer,
            downloadUrl = "",
            releaseNotes = ""
        )
    }

    fun openUpdateUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isVersionNewer(current: String, latest: String): Boolean {
        if (latest.isBlank()) return false
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
            val c = currentParts.getOrElse(i) { 0 }
            val l = latestParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (c > l) return false
        }
        return false
    }
}
