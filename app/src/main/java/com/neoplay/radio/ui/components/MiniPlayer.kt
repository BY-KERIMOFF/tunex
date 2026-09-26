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
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.neoplay.radio.player.PlayerStatus

@Composable
fun MiniPlayer(
    station: Station,
    playerStatus: PlayerStatus,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onMiniPlayerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onMiniPlayerClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = station.favicon,
                contentDescription = station.name,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
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
                    text = station.country.ifBlank { "Canlı Yayım / Live" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (playerStatus == PlayerStatus.BUFFERING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                IconButton(onClick = onPlayPauseClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (playerStatus == PlayerStatus.PLAYING) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onStopClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
