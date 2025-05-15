package ir.noormohammadi.sampleplayer.presentation.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.noormohammadi.sampleplayer.domain.usecase.LoadMediaUseCase
import ir.noormohammadi.sampleplayer.presentation.data.MediaDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaViewModel(private val loadMediaUseCase: LoadMediaUseCase) : ViewModel() {
    private val _mediaItems = MutableLiveData<List<MediaDataModel>>()
    val mediaItems: LiveData<List<MediaDataModel>> get() = _mediaItems

    fun loadMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            val mediaList = loadMediaUseCase.invoke()
            mediaList.map { media ->
                if (media.isVideo) {
                    MediaDataModel.Video(
                        uri = media.uri,
                        thumbnail = media.thumbnail,
                        isPlaying = false,
                        togglePlay = {
                            toggleVideoPlay(uri = it)
                        }
                    )
                } else {
                    MediaDataModel.Image(
                        uri = media.uri,
                        thumbnail = media.thumbnail,
                        isFiltered = false,
                        toggleFilter = {
                            toggleFilter(it)
                        }
                    )
                }
            }.let {
                _mediaItems.postValue(it)
            }
        }
    }

    private fun toggleFilter(uri: Uri) {
        _mediaItems.value = _mediaItems.value?.map {
            if (it is MediaDataModel.Image && it.uri == uri) {
                it.copy(isFiltered = !it.isFiltered)
            } else it
        }
    }

    private fun toggleVideoPlay(uri: Uri) {
        _mediaItems.value = _mediaItems.value?.map {
            if (it is MediaDataModel.Video) {
                it.copy(isPlaying = it.uri == uri)
            } else it
        }
    }
}