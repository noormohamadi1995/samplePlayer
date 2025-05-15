package ir.noormohammadi.sampleplayer.data.repository

import ir.noormohammadi.sampleplayer.domain.repository.MediaRepository
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.io.FileNotFoundException
import java.io.FileOutputStream
import ir.noormohammadi.sampleplayer.domain.model.Media
import androidx.core.graphics.scale


class MediaRepositoryImpl(private val context: Context) : MediaRepository {
    override suspend fun getAllMedia(): List<Media> {
        val mediaList = mutableListOf<Media>()
        val uri = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        context.contentResolver.query(
            uri, projection, selection, selectionArgs, "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val mediaType = cursor.getInt(typeCol)
                val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO

                val contentUri = if (isVideo) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val mediaUri = ContentUris.withAppendedId(contentUri, id)
                val thumbnailUri = generateThumbnail(mediaUri, isVideo)

                mediaList.add(Media(uri = mediaUri, thumbnailUri = thumbnailUri, isVideo = isVideo))
            }
        }

        return mediaList
    }

    // متد ایجاد Thumbnail به صورت دستی
    private fun generateThumbnail(mediaUri: Uri, isVideo: Boolean): Uri {
        return try {
            val thumbnailBitmap = if (isVideo) {
                getVideoThumbnail(mediaUri)
            } else {
                getImageThumbnail(mediaUri)
            }

            saveThumbnailToCache(thumbnailBitmap)
        } catch (e: Exception) {
            mediaUri // اگر Thumbnail ایجاد نشد، URI اصلی بازگردانده می‌شود
        }
    }

    // ایجاد Thumbnail برای تصاویر
    private fun getImageThumbnail(uri: Uri): Bitmap {
        return context.contentResolver.openInputStream(uri)?.use {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri).scale(200, 200)
        } ?: throw FileNotFoundException("Image not found")
    }

    // ایجاد Thumbnail برای ویدیوها
    private fun getVideoThumbnail(uri: Uri): Bitmap {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        return retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)!!.run {
            this.scale(200, 200)
        }
    }

    // ذخیره‌ی Bitmap به عنوان فایل کش و بازگرداندن URI آن
    private fun saveThumbnailToCache(bitmap: Bitmap): Uri {
        val cacheDir = context.cacheDir
        val thumbnailFile = java.io.File(cacheDir, "thumb_${System.currentTimeMillis()}.jpg")
        FileOutputStream(thumbnailFile).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        }
        return Uri.fromFile(thumbnailFile)
    }
}
