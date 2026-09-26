package com.neoplay.radio.ui.tv

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
    onBackClick: () -> Unit = {},
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerStatus by viewModel.playerStatus.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()

    val activeStation = currentStation ?: station

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            var isBackFocused by remember { mutableStateOf(false) }

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .onFocusChanged { isBackFocused = it.isFocused }
                    .border(
                        width = if (isBackFocused) 2.dp else 0.dp,
                        color = if (isBackFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isBackFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SubcomposeAsyncImage(
            model = activeStation?.favicon,
            contentDescription = activeStation?.name,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop,
            error = {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = activeStation?.displayTitle ?: "Neo Radio TV",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = activeStation?.country.orEmpty(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.weight(1f))

        PlayerControls(
            playerStatus = playerStatus,
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onStopClick = { viewModel.stop() },
            onNextClick = { viewModel.playNext() },
            onPreviousClick = { viewModel.playPrevious() }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
