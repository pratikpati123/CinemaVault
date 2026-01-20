package com.example.cinemavault.data.repository

import androidx.room.withTransaction
import com.example.cinemavault.core.common.Resource
import com.example.cinemavault.data.local.MoviesDatabase
import com.example.cinemavault.data.local.entity.MovieEntity
import com.example.cinemavault.data.mapper.toDomain
import com.example.cinemavault.data.mapper.toEntity
import com.example.cinemavault.data.remote.dto.MovieDTO
import com.example.cinemavault.data.remote.dto.TmdbApi
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val db: MoviesDatabase
) : MovieRepository {

    private val dao = db.movieDao()

    override fun getTrendingMovies(): Flow<List<Movie>> {
        return dao.getTrendingMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNowPlayingMovies(): Flow<List<Movie>> {
        return dao.getNowPlayingMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshTrendingMovies(): Resource<Unit> {
        return try {
            val response = api.getTrendingMovies()

            saveMoviesWithMerge(
                remoteMovies = response.results,
                clearFlagAction = { dao.clearTrending() },
                mergeMapper = { dto, existing ->
                    dto.toEntity(
                        isTrending = true,
                        isNowPlaying = existing?.isNowPlaying ?: false,
                        isBookmarked = existing?.isBookmarked ?: false
                    )
                }
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is kotlin.coroutines.cancellation.CancellationException) throw e

            Resource.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun refreshNowPlayingMovies(): Resource<Unit> {
        return try {
            val response = api.getNowPlayingMovies()

            saveMoviesWithMerge(
                remoteMovies = response.results,
                clearFlagAction = { dao.clearNowPlaying() },
                mergeMapper = { dto, existing ->
                    dto.toEntity(
                        isTrending = existing?.isTrending ?: false,
                        isNowPlaying = true,
                        isBookmarked = existing?.isBookmarked ?: false
                    )
                }
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is kotlin.coroutines.cancellation.CancellationException) throw e

            Resource.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override fun getBookmarkedMovies(): Flow<List<Movie>> {
        return dao.getBookmarkedMovies().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getMovieById(id: Int): Resource<Movie> {
        return try {
            val local = dao.getMovieById(id)
            if (local != null) return Resource.Success(local.toDomain())

            val remote = api.getMovieDetails(id)
            dao.insertSingleMovie(remote.toEntity(
                isTrending = false,
                isNowPlaying = false,
                isBookmarked = false
            ))
            Resource.Success(remote.toEntity(
                isTrending = false,
                isNowPlaying = false,
                isBookmarked = false
            ).toDomain())
        } catch (_: Exception) {
            Resource.Error("Error")
        }
    }

    override suspend fun toggleBookmark(id: Int, isBookmarked: Boolean) {
        dao.updateBookmark(id, isBookmarked)
    }

    override suspend fun searchMovies(query: String): Flow<List<Movie>> = kotlinx.coroutines.flow.flow {
        try {
            val response = api.searchMovies(query)
            val movieIds = response.results.map { it.id }
            val existingMap = dao.getMoviesByIds(movieIds).associateBy { it.id }

            val movies = response.results.map { dto ->
                val existing = existingMap[dto.id]
                dto.toEntity(
                    isTrending = false,
                    isNowPlaying = false,
                    isBookmarked = existing?.isBookmarked ?: false
                ).toDomain()
            }

            emit(movies)

        } catch (_: Exception) {
            val localResults = dao.searchMovies(query).first()
            emit(localResults.map { it.toDomain() })
        }
    }

    /**
     * A generic helper to handle the "Safe Merge" strategy.
     * * @param remoteMovies The fresh list of movies from the API.
     * @param clearFlagAction A lambda to clear the specific flag (e.g., clearTrending or clearNowPlaying).
     * @param mergeMapper A lambda that defines how to merge the new DTO with the (optional) existing Entity.
     */
    private suspend fun saveMoviesWithMerge(
        remoteMovies: List<MovieDTO>,
        clearFlagAction: suspend () -> Unit,
        mergeMapper: (MovieDTO, MovieEntity?) -> MovieEntity
    ) {
        db.withTransaction {
            clearFlagAction()

            val movieIds = remoteMovies.map { it.id }
            val existingMap = dao.getMoviesByIds(movieIds).associateBy { it.id }

            val mergedList = remoteMovies.map { dto ->
                val existing = existingMap[dto.id]
                mergeMapper(dto, existing)
            }

            dao.insertMovies(mergedList)
        }
    }
}