package com.neoplay.radio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.data.repository.RadioRepository
import com.neoplay.radio.player.RadioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StationsUiState(
    val categoryName: String = "",
    val stations: List<Station> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val radioRepository: RadioRepository,
    private val radioPlayer: RadioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationsUiState())
    val uiState: StateFlow<StationsUiState> = _uiState.asStateFlow()

    private val countryNames = setOf(
        "azerbaijan", "azərbaycan", "az",
        "turkey", "türkiye", "turkiye", "tr",
        "russia", "ru", "россия",
        "germany", "de", "deutschland",
        "united states", "usa", "us", "america",
        "united kingdom", "uk", "gb", "england",
        "france", "fr",
        "spain", "es",
        "italy", "it",
        "canada", "ca",
        "australia", "au",
        "brazil", "br",
        "japan", "jp",
        "south korea", "kr",
        "china", "cn",
        "india", "in",
        "netherlands", "nl",
        "sweden", "se",
        "switzerland", "ch",
        "norway", "no",
        "poland", "pl",
        "ukraine", "ua",
        "georgia", "ge",
        "kazakhstan", "kz",
        "uzbekistan", "uz",
        "united arab emirates", "ae", "dubai",
        "saudi arabia", "sa",
        "mexico", "mx",
        "argentina", "ar",
        "greece", "gr"
    )

    fun loadCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(categoryName = category, isLoading = true)
            try {
                // Treat everything in Category screen as a Country query
                val flow = radioRepository.getStationsFlow(country = category)
                flow.collect { list ->
                    _uiState.value = _uiState.value.copy(stations = list, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun playStationWithList(station: Station) {
        radioPlayer.playStation(station)
    }

    fun toggleFavorite(station: Station) {
        viewModelScope.launch {
            radioRepository.toggleFavorite(station.stationuuid)
        }
    }
}
