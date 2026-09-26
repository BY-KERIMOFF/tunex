package com.neoplay.radio.ui.tv

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.ui.components.LoadingIndicator
import com.neoplay.radio.viewmodel.HomeViewModel

@Composable
fun TvHomeScreen(
    onCategoryClick: (String) -> Unit,
    onStationClick: (Station) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Neo Radio TV",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "World Countries / Ölkələr",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            val tvCountries = listOf(
                "Azerbaijan" to "🇦🇿",
                "Turkey" to "🇹🇷",
                "Russia" to "🇷🇺",
                "United States" to "🇺🇸",
                "United Kingdom" to "🇬🇧",
                "Germany" to "🇩🇪",
                "France" to "🇫🇷",
                "Italy" to "🇮🇹",
                "Spain" to "🇪🇸"
            )
            items(tvCountries) { (cat, flag) ->
                Card(
                    modifier = Modifier
                        .height(90.dp)
                        .clickable { onCategoryClick(cat) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = flag, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = cat, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        Text(
            text = "Popular Stations / Məşhur Radiolar",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.topStations) { station ->
                    Card(
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth(0.35f)
                            .clickable { onStationClick(station) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = station.displayTitle, style = MaterialTheme.typography.titleMedium)
                            Text(text = station.country, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
