package com.barinov.simpleplayer.domain

import com.barinov.simpleplayer.data.PlaylistsDao

class PlaylistRepository(
    private val dao: PlaylistsDao
) {

    fun getClaimedPlaylistsCount() = dao.getClaimedPlaylistsFlowCount()
}