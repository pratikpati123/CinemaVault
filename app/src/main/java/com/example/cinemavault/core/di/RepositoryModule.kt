package com.example.cinemavault.core.di

import com.example.cinemavault.data.repository.MovieRepositoryImpl
import com.example.cinemavault.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for binding repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the [MovieRepository] interface to its [MovieRepositoryImpl] implementation.
     *
     * @param movieRepositoryImpl The implementation of the movie repository.
     * @return The bound [MovieRepository] interface.
     */
    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}
