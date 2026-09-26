package com.neoplay.radio.ui.phone

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neoplay.radio.R
import com.neoplay.radio.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.tab_settings),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = stringResource(id = R.string.settings_theme), style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = theme == "dark", onClick = { viewModel.setTheme("dark") })
            Text(text = stringResource(id = R.string.theme_dark))

            RadioButton(selected = theme == "light", onClick = { viewModel.setTheme("light") })
            Text(text = stringResource(id = R.string.theme_light))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = stringResource(id = R.string.settings_language), style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            listOf("az" to "AZ", "en" to "EN", "tr" to "TR", "ru" to "RU").forEach { (code, label) ->
                RadioButton(selected = language == code, onClick = { viewModel.setLanguage(code) })
                Text(text = label, modifier = Modifier.padding(end = 12.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.clearCache()
                Toast.makeText(context, context.getString(R.string.settings_cache_cleared), Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.settings_clear_cache))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Neo Radio v1.0.0 (Global Edition)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
