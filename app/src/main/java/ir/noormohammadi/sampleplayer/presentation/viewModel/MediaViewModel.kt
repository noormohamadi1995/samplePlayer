package ir.noormohammadi.sampleplayer.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.noormohammadi.sampleplayer.domain.model.Media
import ir.noormohammadi.sampleplayer.domain.usecase.LoadMediaUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaViewModel (private val loadMediaUseCase: LoadMediaUseCase) : ViewModel() {
    private val _mediaList = MutableLiveData<List<Media>>()
    val mediaList: LiveData<List<Media>> get() = _mediaList

    fun loadMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            val media = loadMediaUseCase.invoke()
            _mediaList.postValue(media)
        }
    }
}