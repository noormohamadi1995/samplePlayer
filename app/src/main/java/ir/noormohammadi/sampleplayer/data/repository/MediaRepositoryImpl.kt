package ir.noormohammadi.sampleplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import ir.noormohammadi.sampleplayer.data.model.MediaItem
import ir.noormohammadi.sampleplayer.domain.model.Media
import ir.noormohammadi.sampleplayer.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale


class MediaRepositoryImpl(private val context: Context) : MediaRepository {
    override suspend fun getAllMedia(): List<Media> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<Media>()
        val collection = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
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
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

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
                val thumbnail = getThumbnail(uri, isVideo)
                mediaList.add(Media(uri, thumbnail = thumbnail, isVideo = isVideo))
            }
        }
        mediaList
    }

    // متد بهینه دریافت Thumbnail (تصویر کوچک)
    private fun getThumbnail(uri: Uri, isVideo: Boolean): Bitmap? {
        val thumbnailSize = Size(200, 200) // اندازه ثابت برای همه‌ی Thumbnailها

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                context.contentResolver.loadThumbnail(uri, thumbnailSize, null)
            } catch (e: Exception) {
                null
            }
        } else {
            if (isVideo) {
                getVideoThumbnail(uri, thumbnailSize)
            } else {
                getImageThumbnail(uri, thumbnailSize)
            }
        }
    }

    // دریافت Thumbnail برای ویدیوها (نسخه‌های پایین‌تر)
    private fun getVideoThumbnail(uri: Uri, size: Size): Bitmap? {
        val filePath = getFilePath(uri)
        return filePath?.let {
            ThumbnailUtils.createVideoThumbnail(it, MediaStore.Video.Thumbnails.MINI_KIND)
                ?.scale(size.width, size.height)
        }
    }

    // دریافت Thumbnail برای تصاویر (نسخه‌های پایین‌تر)
    private fun getImageThumbnail(uri: Uri, size: Size): Bitmap? {
        val filePath = getFilePath(uri)
        return filePath?.let {
            BitmapFactory.decodeFile(it)?.scale(size.width, size.height)
        }
    }

    // دریافت مسیر فایل برای نسخه‌های پایین‌تر
    private fun getFilePath(uri: Uri): String? {
        return try {
            val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                if (cursor.moveToFirst()) cursor.getString(dataIndex) else null
            }
        } catch (e: Exception) {
            null
        }
    }
}
