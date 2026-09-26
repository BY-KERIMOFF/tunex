package com.neoplay.radio.ui.phone

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
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.neoplay.radio.R
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.ui.components.PlayerControls
import com.neoplay.radio.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    station: Station?,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerStatus by viewModel.playerStatus.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsState()

    val activeStation = currentStation ?: station
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            IconButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Default.Timer, contentDescription = "Sleep Timer")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = activeStation?.displayTitle ?: "Neo Radio",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = activeStation?.country.orEmpty(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        if (sleepTimerMinutes > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stringResource(id = R.string.sleep_timer)}: $sleepTimerMinutes min",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PlayerControls(
            playerStatus = playerStatus,
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onStopClick = { viewModel.stop() },
            onNextClick = { viewModel.playNext() },
            onPreviousClick = { viewModel.playPrevious() }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.sleep_timer),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                listOf(15, 30, 60, 90, 0).forEach { mins ->
                    Button(
                        onClick = {
                            viewModel.setSleepTimer(mins)
                            showBottomSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = if (mins == 0) stringResource(id = R.string.sleep_timer_off) else "$mins mins"
                        )
                    }
                }
            }
        }
    }
}
