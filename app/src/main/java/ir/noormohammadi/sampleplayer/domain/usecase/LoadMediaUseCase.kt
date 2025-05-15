package ir.noormohammadi.sampleplayer.domain.usecase

import ir.noormohammadi.sampleplayer.domain.model.Media
import ir.noormohammadi.sampleplayer.domain.repository.MediaRepository

class LoadMediaUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(): List<Media> {
        return repository.getAllMedia()
    }
}