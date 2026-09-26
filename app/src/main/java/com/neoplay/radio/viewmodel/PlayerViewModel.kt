package com.neoplay.radio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoplay.radio.data.model.Station
import com.neoplay.radio.data.repository.RadioRepository
import com.neoplay.radio.player.PlayerStatus
import com.neoplay.radio.player.RadioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val radioPlayer: RadioPlayer,
    private val radioRepository: RadioRepository
) : ViewModel() {

    val playerStatus: StateFlow<PlayerStatus> = radioPlayer.status
    val currentStation: StateFlow<Station?> = radioPlayer.nowPlayingStation
    val sleepTimerMinutes: StateFlow<Int> = radioPlayer.sleepTimerMinutes

    private val _playlist = MutableStateFlow<List<Station>>(emptyList())
    val playlist: StateFlow<List<Station>> = _playlist.asStateFlow()

    fun setPlaylist(stations: List<Station>) {
        if (stations.isNotEmpty()) {
            _playlist.value = stations
        }
    }

    fun playStation(station: Station, stations: List<Station> = emptyList()) {
        if (stations.isNotEmpty()) {
            _playlist.value = stations
        }
        val currentList = if (_playlist.value.isNotEmpty()) _playlist.value else radioRepository.getCachedStations()
        if (currentList.none { it.stationuuid == station.stationuuid }) {
            _playlist.value = currentList + station
        } else if (_playlist.value.isEmpty()) {
            _playlist.value = currentList
        }
        radioPlayer.playStation(station)
    }

    fun playNext() {
        val current = currentStation.value ?: return
        val list = if (_playlist.value.isNotEmpty()) _playlist.value else radioRepository.getCachedStations()
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.stationuuid == current.stationuuid }
        if (currentIndex != -1) {
            val nextIndex = (currentIndex + 1) % list.size
            radioPlayer.playStation(list[nextIndex])
        } else {
            radioPlayer.playStation(list[0])
        }
    }

    fun playPrevious() {
        val current = currentStation.value ?: return
        val list = if (_playlist.value.isNotEmpty()) _playlist.value else radioRepository.getCachedStations()
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.stationuuid == current.stationuuid }
        if (currentIndex != -1) {
            val prevIndex = if (currentIndex - 1 < 0) list.size - 1 else currentIndex - 1
            radioPlayer.playStation(list[prevIndex])
        } else {
            radioPlayer.playStation(list.last())
        }
    }

    fun togglePlayPause() {
        radioPlayer.togglePlayPause()
    }

    fun stop() {
        radioPlayer.stop()
    }

    fun setSleepTimer(minutes: Int) {
        viewModelScope.launch {
            radioPlayer.setSleepTimer(minutes)
        }
    }
}
