package ir.noormohammadi.sampleplayer.presentation.data

import android.net.Uri

sealed class MediaDataModel {
     data class Image(
         val uri: Uri,
         val thumbnail: Uri?,
         val isFiltered: Boolean = false,
         val toggleFilter : (Uri) -> Unit
     ) : MediaDataModel(){
         companion object {
             const val VIEW_TYPE = 0
         }
     }

     data class Video(
         val uri: Uri,
         val thumbnail: Uri?,
         val isPlaying: Boolean = false,
         val togglePlay : (Uri) -> Unit
     ) : MediaDataModel(){
         companion object {
             const val VIEW_TYPE = 1
         }
     }
}