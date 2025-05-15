package ir.noormohammadi.sampleplayer.domain.model

import android.graphics.Bitmap
import android.net.Uri

sealed interface Media {
    val uri: Uri
    val thumbnail: Uri?

    data class ImageMedia(
        override val uri: Uri,
        override val thumbnail: Uri?
    ) : Media

    data class VideoMedia(
        override val uri: Uri,
        override val thumbnail: Uri?
    ) : Media
}
