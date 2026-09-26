package com.neoplay.radio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.data.repository.RadioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val radioRepository: RadioRepository
) : ViewModel() {

    val favoritesFlow: StateFlow<List<Station>> = radioRepository.getFavoriteStations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(station: Station) {
        viewModelScope.launch {
            radioRepository.toggleFavorite(station.stationuuid)
        }
    }
}
