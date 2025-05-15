package ir.noormohammadi.sampleplayer.data.model

import android.net.Uri

data class MediaItem(
    val uri: Uri,
    val isVideo: Boolean,
    val thumbnail : Uri
)
