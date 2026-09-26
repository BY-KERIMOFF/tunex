package com.neoplay.radio.ui.phone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryScreen(
    onCategoryClick: (String) -> Unit
) {
    val worldCountries = listOf(
        "Azerbaijan" to "🇦🇿",
        "Türkiye" to "🇹🇷",
        "Russia" to "🇷🇺",
        "United States" to "🇺🇸",
        "United Kingdom" to "🇬🇧",
        "Germany" to "🇩🇪",
        "France" to "🇫🇷",
        "Italy" to "🇮🇹",
        "Spain" to "🇪🇸",
        "Canada" to "🇨🇦",
        "Australia" to "🇦🇺",
        "Brazil" to "🇧🇷",
        "Japan" to "🇯🇵",
        "South Korea" to "🇰🇷",
        "China" to "🇨🇳",
        "India" to "🇮🇳",
        "Netherlands" to "🇳🇱",
        "Sweden" to "🇸🇪",
        "Switzerland" to "🇨🇭",
        "Norway" to "🇳🇴",
        "Poland" to "🇵🇱",
        "Ukraine" to "🇺🇦",
        "Georgia" to "🇬🇪",
        "Kazakhstan" to "🇰🇿",
        "Uzbekistan" to "🇺🇿",
        "United Arab Emirates" to "🇦🇪",
        "Saudi Arabia" to "🇸🇦",
        "Mexico" to "🇲🇽",
        "Argentina" to "🇦🇷",
        "Greece" to "🇬🇷"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bütün Dünya Ölkələri / World Countries",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(worldCountries) { (country, flag) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(country) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = flag, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = country,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
