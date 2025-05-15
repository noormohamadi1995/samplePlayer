package ir.noormohammadi.sampleplayer.data.repository

import ir.noormohammadi.sampleplayer.data.source.MediaDataSource
import ir.noormohammadi.sampleplayer.domain.model.Media
import ir.noormohammadi.sampleplayer.domain.repository.MediaRepository


class MediaRepositoryImpl(
    private val dataSource: MediaDataSource
) : MediaRepository {
    override suspend fun getAllMedia(): List<Media> {
        return dataSource.getMedia()
    }

    override suspend fun getImages(): List<Media> {
        return dataSource.getMedia().filterIsInstance<Media.ImageMedia>()

    }

    override suspend fun getVideos(): List<Media> {
        return dataSource.getMedia().filterIsInstance<Media.VideoMedia>()
    }
}
