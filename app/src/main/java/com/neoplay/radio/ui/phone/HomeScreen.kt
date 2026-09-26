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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neoplay.radio.R
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.ui.components.LoadingIndicator
import com.neoplay.radio.ui.components.StationCard
import com.neoplay.radio.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit,
    onStationClick: (Station) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val update = uiState.updateInfo
    if (update != null && update.hasUpdate) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUpdate() },
            title = {
                Text(
                    text = "Yeni Yenilənmə Var! (${update.latestVersion})",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = "Tətbiqin yeni versiyası mövcuddur. İndi yeniləmək istəyirsiniz?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (update.releaseNotes.isNotBlank()) {
                        Text(
                            text = "\nYeniliklər:\n${update.releaseNotes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.openUpdateUrl(update.downloadUrl)
                        viewModel.dismissUpdate()
                    }
                ) {
                    Text("Yenilə / Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissUpdate() }) {
                    Text("Sonra")
                }
            }
        )
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.tab_categories),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    val categories = listOf(
                        "Azerbaijan" to "🇦🇿",
                        "Turkey" to "🇹🇷",
                        "Russia" to "🇷🇺",
                        "United States" to "🇺🇸",
                        "United Kingdom" to "🇬🇧",
                        "Germany" to "🇩🇪"
                    )
                    items(categories) { (category, flag) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategoryClick(category) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = flag, style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.popular_stations),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(uiState.topStations) { station ->
                StationCard(
                    station = station,
                    onStationClick = onStationClick,
                    onFavoriteToggle = { viewModel.toggleFavorite(it) }
                )
            }
        }
    }
}
