package com.barinov.simpleplayer.domain.model

import com.barinov.simpleplayer.domain.RootType
import java.io.File
import java.util.UUID

data class VerifiedTrackItem(
    val signatureString: String,
    val len: Long,
    val rootType: RootType,
    val iFile: File?,
    val uEntity: CommonFileItem.UsbData?,
    val id: UUID = UUID.randomUUID(),
)
