package com.neoplay.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.neoplay.radio.ui.theme.NeoRadioTheme
import com.neoplay.radio.ui.tv.TvHomeScreen
import com.neoplay.radio.viewmodel.PlayerViewModel
import com.neoplay.radio.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvMainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeSetting by settingsViewModel.theme.collectAsState()

            NeoRadioTheme(themeSetting = themeSetting) {
                TvHomeScreen(
                    onCategoryClick = { },
                    onStationClick = { station -> playerViewModel.playStation(station) }
                )
            }
        }
    }
}
