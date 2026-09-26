package com.neoplay.radio.ui.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.ui.components.PlayerControls
import com.neoplay.radio.viewmodel.PlayerViewModel

@Composable
fun TvPlayerScreen(
    station: Station?,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerStatus by viewModel.playerStatus.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()

    val activeStation = currentStation ?: station

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SubcomposeAsyncImage(
            model = activeStation?.favicon,
            contentDescription = activeStation?.name,
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop,
            error = {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = activeStation?.displayTitle ?: "Neo Radio TV",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = activeStation?.country.orEmpty(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        PlayerControls(
            playerStatus = playerStatus,
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onStopClick = { viewModel.stop() },
            onNextClick = { viewModel.playNext() },
            onPreviousClick = { viewModel.playPrevious() }
        )
    }
}
