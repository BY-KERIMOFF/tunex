package com.neoplay.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.ui.theme.NeoRadioTheme
import com.neoplay.radio.ui.tv.TvHomeScreen
import com.neoplay.radio.ui.tv.TvPlayerScreen
import com.neoplay.radio.ui.tv.TvStationsScreen
import com.neoplay.radio.viewmodel.PlayerViewModel
import com.neoplay.radio.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed class TvScreen {
    object Home : TvScreen()
    data class Stations(val category: String) : TvScreen()
    data class Player(val station: Station) : TvScreen()
}

@AndroidEntryPoint
class TvMainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeSetting by settingsViewModel.theme.collectAsState()
            var currentTvScreen by remember { mutableStateOf<TvScreen>(TvScreen.Home) }

            BackHandler(enabled = currentTvScreen != TvScreen.Home) {
                currentTvScreen = when (currentTvScreen) {
                    is TvScreen.Player -> {
                        val activeStation = (currentTvScreen as TvScreen.Player).station
                        TvScreen.Stations(activeStation.country.ifBlank { "Azerbaijan" })
                    }
                    is TvScreen.Stations -> TvScreen.Home
                    TvScreen.Home -> TvScreen.Home
                }
            }

            NeoRadioTheme(themeSetting = themeSetting) {
                when (val screen = currentTvScreen) {
                    TvScreen.Home -> {
                        TvHomeScreen(
                            onCategoryClick = { category ->
                                currentTvScreen = TvScreen.Stations(category)
                            },
                            onStationClick = { station ->
                                playerViewModel.playStation(station)
                                currentTvScreen = TvScreen.Player(station)
                            }
                        )
                    }
                    is TvScreen.Stations -> {
                        TvStationsScreen(
                            categoryName = screen.category,
                            onBackClick = { currentTvScreen = TvScreen.Home },
                            onStationClick = { station ->
                                playerViewModel.playStation(station)
                                currentTvScreen = TvScreen.Player(station)
                            }
                        )
                    }
                    is TvScreen.Player -> {
                        TvPlayerScreen(
                            station = screen.station,
                            onBackClick = {
                                currentTvScreen = TvScreen.Stations(screen.station.country.ifBlank { "Azerbaijan" })
                            }
                        )
                    }
                }
            }
        }
    }
}
