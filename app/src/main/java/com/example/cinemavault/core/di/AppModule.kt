package com.example.cinemavault.core.di

import android.app.Application
import androidx.room.Room
import com.example.cinemavault.core.common.Constants
import com.example.cinemavault.data.local.MoviesDatabase
import com.example.cinemavault.data.local.dao.MovieDao
import com.example.cinemavault.data.remote.dto.TmdbApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides dependencies for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the Room database instance for the application.
     *
     * @param app The application context.
     * @return The singleton instance of [MoviesDatabase].
     */
    @Provides
    @Singleton
    fun provideMoviesDatabase(app: Application): MoviesDatabase {
        return Room.databaseBuilder(
            app,
            MoviesDatabase::class.java,
            "cinema_vault_db"
        ).build()
    }

    /**
     * Provides the Data Access Object (DAO) for the movie table.
     *
     * @param db The [MoviesDatabase] instance.
     * @return The singleton instance of [MovieDao].
     */
    @Provides
    @Singleton
    fun provideMovieDao(db: MoviesDatabase): MovieDao {
        return db.movieDao()
    }

    /**
     * Provides the OkHttpClient for making network requests.
     * It includes a logging interceptor and an interceptor to add the API key to each request.
     *
     * @return The singleton instance of [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url

                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", Constants.API_KEY)
                    .build()

                val requestBuilder = original.newBuilder()
                    .url(url)

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    /**
     * Provides the Retrofit API service for The Movie Database (TMDb).
     *
     * @param client The [OkHttpClient] to use for network requests.
     * @return The singleton instance of [TmdbApi].
     */
    @Provides
    @Singleton
    fun provideTmdbApi(client: OkHttpClient): TmdbApi {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(TmdbApi::class.java)
    }
}
