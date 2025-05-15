package ir.noormohammadi.sampleplayer.domain.model

import android.net.Uri

data class Media(
    val uri : Uri,
    val isVideo : Boolean,
    val thumbnailUri : Uri
)
