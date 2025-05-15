package ir.noormohammadi.sampleplayer.data.source

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import ir.noormohammadi.sampleplayer.domain.model.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MediaDataSource(
    private val context: Context
) {

    suspend fun getMedia(): List<Media> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<Media>()
        val collection = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO

                val contentUri = if (isVideo) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val uri = ContentUris.withAppendedId(contentUri, id)
                val thumbnail = getThumbnailCompat(id, isVideo)
                mediaList.add(
                    if (isVideo) Media.VideoMedia(uri = uri, thumbnail = thumbnail)
                    else Media.ImageMedia(uri = uri, thumbnail = thumbnail)
                )
            }
        }
        mediaList
    }

    private suspend fun getThumbnailCompat(id: Long, isVideo: Boolean): Uri? = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getThumbnailQ(id, isVideo)
        } else {
            getThumbnailLegacy(id, isVideo)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getThumbnailQ(id: Long, isVideo: Boolean): Uri? {
        val uri = if (isVideo) {
            MediaStore.Video.Thumbnails.getThumbnail(
                context.contentResolver, id, MediaStore.Video.Thumbnails.MINI_KIND, null
            )
        } else {
            MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null
            )
        }
        return uri?.let { saveBitmapAsUri(it) }
    }

    private fun getThumbnailLegacy(id: Long, isVideo: Boolean): Uri? {
        val bitmap = if (isVideo) {
            MediaStore.Video.Thumbnails.getThumbnail(
                context.contentResolver, id, MediaStore.Video.Thumbnails.MINI_KIND, null
            )
        } else {
            MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null
            )
        }
        return bitmap?.let { saveBitmapAsUri(it) }
    }

    private fun saveBitmapAsUri(bitmap: Bitmap): Uri? {
        val file = File(context.cacheDir, "temp_thumbnail_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return Uri.fromFile(file)
    }
}