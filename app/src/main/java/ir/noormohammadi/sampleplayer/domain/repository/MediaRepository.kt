package ir.noormohammadi.sampleplayer.domain.repository

import ir.noormohammadi.sampleplayer.domain.model.Media

interface MediaRepository {
    suspend fun getAllMedia(): List<Media>
    suspend fun getImages(): List<Media>
    suspend fun getVideos(): List<Media>
}