package ir.noormohammadi.sampleplayer.data.model

import android.graphics.Bitmap
import android.net.Uri

data class MediaItem(
    val uri: Uri,
    val isVideo: Boolean,
    val thumbnail: Bitmap?,
)
