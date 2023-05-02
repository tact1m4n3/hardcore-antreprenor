package com.anonymous.prohiking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ExploreViewModel: ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText= _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false )
    val isSearching = _isSearching.asStateFlow()

    private val _tracks = MutableStateFlow(allTracks)
    val tracks = searchText
        .debounce(1000L)
        .onEach { _isSearching.update {true} }
        .combine(_tracks ) { text, tracks ->
            if(text.isBlank()) {
                tracks
            } else{
                tracks.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update{false} }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _tracks.value
        )

    fun onSearchTextChange(text: String) {
            _searchText.value=text
    }
}

data class Track(
    val trackName: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$trackName",
            "${trackName.first()}"
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allTracks= listOf(
    Track (
        trackName= "Cabana Omu"
    ),
    Track (
        trackName = "Cabana Diham"
    ),
    Track (
        trackName = "Moroeni - Plaiul Proporului - Cabana Scropoasa"
    ),
    Track (
        trackName = "Sinaia- Bucegi- Platoul Masivului Bucegi - Sinaia"

    )
)