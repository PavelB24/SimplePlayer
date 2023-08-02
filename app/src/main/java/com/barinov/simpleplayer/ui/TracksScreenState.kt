package com.barinov.simpleplayer.ui

sealed interface TracksScreenState {

    object Idle: TracksScreenState

    object Paused: TracksScreenState

    data class Playing(val id: String): TracksScreenState
}