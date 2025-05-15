package ir.noormohammadi.sampleplayer.di

import ir.noormohammadi.sampleplayer.data.repository.MediaRepositoryImpl
import ir.noormohammadi.sampleplayer.data.source.MediaDataSource
import ir.noormohammadi.sampleplayer.domain.repository.MediaRepository
import ir.noormohammadi.sampleplayer.domain.usecase.LoadMediaUseCase
import ir.noormohammadi.sampleplayer.presentation.viewModel.MediaViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<MediaDataSource> { MediaDataSource(androidContext()) }
    factory<MediaRepository> { MediaRepositoryImpl(get()) }
    factory { LoadMediaUseCase(get()) }
    viewModel { MediaViewModel(get()) }
}