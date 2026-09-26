package com.neoplay.radio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.ui.theme.AccentPink

@Composable
fun StationCard(
    station: Station,
    onStationClick: (Station) -> Unit,
    onFavoriteToggle: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    val flagEmoji = getCountryFlagEmoji(station.countrycode, station.country)
    val subtitleText = listOf("$flagEmoji ${station.country}", station.formattedBitrate)
        .filter { it.isNotBlank() && it != "📻 " }
        .joinToString(" • ")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onStationClick(station) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = station.favicon,
                contentDescription = station.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitleText.ifBlank { station.country.ifBlank { "Radio" } },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { onFavoriteToggle(station) }) {
                Icon(
                    imageVector = if (station.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (station.isFavorite) AccentPink else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

fun getCountryFlagEmoji(countryCode: String, countryName: String): String {
    val code = countryCode.uppercase()
    if (code.length == 2) {
        try {
            val firstLetter = Character.codePointAt(code, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(code, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        } catch (_: Exception) {}
    }
    return when (countryName.lowercase().trim()) {
        "azerbaijan", "azərbaycan", "az" -> "🇦🇿"
        "turkey", "türkiye", "tr", "turkiye" -> "🇹🇷"
        "russia", "ru", "россия" -> "🇷🇺"
        "germany", "de", "deutschland" -> "🇩🇪"
        "usa", "united states", "us", "america" -> "🇺🇸"
        "uk", "united kingdom", "gb", "england" -> "GB"
        "france", "fr" -> "🇫🇷"
        "spain", "es" -> "ES"
        "italy", "it" -> "🇮🇹"
        else -> "📻"
    }
}
