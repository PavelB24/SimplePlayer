package com.barinov.simpleplayer.ui.uiModels

import java.util.UUID


data class SelectableSearchedItem(
    val uuid: UUID,
    val name: String,
    val checked: Boolean
)
