package com.neoplay.radio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.data.repository.RadioRepository
import com.neoplay.radio.data.update.UpdateChecker
import com.neoplay.radio.data.update.UpdateInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val topStations: List<Station> = emptyList(),
    val favoriteStations: List<Station> = emptyList(),
    val updateInfo: UpdateInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val radioRepository: RadioRepository,
    private val updateChecker: UpdateChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        checkForUpdates()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                radioRepository.getStationsFlow().collect { stations ->
                    _uiState.value = _uiState.value.copy(
                        topStations = stations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
            }
        }
    }

    private fun checkForUpdates() {
        viewModelScope.launch {
            val update = updateChecker.checkForUpdates()
            if (update.hasUpdate) {
                _uiState.value = _uiState.value.copy(updateInfo = update)
            }
        }
    }

    fun openUpdateUrl(url: String) {
        updateChecker.openUpdateUrl(url)
    }

    fun dismissUpdate() {
        _uiState.value = _uiState.value.copy(updateInfo = null)
    }

    fun toggleFavorite(station: Station) {
        viewModelScope.launch {
            radioRepository.toggleFavorite(station.stationuuid)
        }
    }
}
