package com.example.cinemavault.data.repository

import androidx.room.withTransaction
import com.example.cinemavault.core.common.Resource
import com.example.cinemavault.data.local.MoviesDatabase
import com.example.cinemavault.data.mapper.toDomain
import com.example.cinemavault.data.mapper.toEntity
import com.example.cinemavault.data.remote.TmdbApi
import com.example.cinemavault.domain.model.Movie
import com.example.cinemavault.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [MovieRepository] that uses a remote API and a local database as data sources.
 *
 * @property api The remote API for fetching movie data.
 * @property db The local database for caching movie data.
 */
@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val db: MoviesDatabase
) : MovieRepository {

    private val dao = db.movieDao()

    /**
     * Fetches the list of trending movies.
     *
     * It first emits the locally cached data, then fetches the latest data from the remote API.
     * If the fetch is successful, it updates the local database and emits the updated data.
     *
     * @param forceFetch If true, forces a fetch from the remote API, otherwise fetches only if local data is empty.
     * @return A [Flow] of [Resource] containing the list of trending [Movie]s.
     */
    override fun getTrendingMovies(forceFetch: Boolean): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading(true))

        val localData = dao.getTrendingMovies().first()
        emit(Resource.Success(localData.map { it.toDomain() }))
        
        val shouldFetch = forceFetch || localData.isEmpty()
        if (shouldFetch) {
            try {
                val response = api.getTrendingMovies()
                val remoteMovies = response.results

                db.withTransaction {
                    val bookmarkedIds = dao.getBookmarkedMovies().first().map { it.id }.toSet()

                    dao.clearTrending()
                    val entities = remoteMovies.map { dto ->
                        dto.toEntity(
                            isTrending = true,
                            isNowPlaying = false,
                            isBookmarked = bookmarkedIds.contains(dto.id)
                        )
                    }
                    dao.insertMovies(entities)
                }
            } catch (e: Exception) {
                emit(Resource.Error("Could not refresh data: ${e.localizedMessage}", localData.map { it.toDomain() }))
            }
        }

        val updatedLocalData = dao.getTrendingMovies().first()
        emit(Resource.Success(updatedLocalData.map { it.toDomain() }))

        emit(Resource.Loading(false))
    }

    /**
     * Fetches the list of movies that are currently playing in theaters.
     *
     * It first emits the locally cached data, then fetches the latest data from the remote API.
     * If the fetch is successful, it updates the local database and emits the updated data.
     *
     * @param forceFetch If true, forces a fetch from the remote API, otherwise fetches only if local data is empty.
     * @return A [Flow] of [Resource] containing the list of now playing [Movie]s.
     */
    override fun getNowPlayingMovies(forceFetch: Boolean): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading(true))

        val localData = dao.getNowPlayingMovies().first()
        emit(Resource.Success(localData.map { it.toDomain() }))

        val shouldFetch = forceFetch || localData.isEmpty()

        if (shouldFetch) {
            try {
                val response = api.getNowPlayingMovies()
                val remoteMovies = response.results

                db.withTransaction {

                    val bookmarkedIds = dao.getBookmarkedMovies().first().map { it.id }.toSet()
                    dao.clearNowPlaying()

                    val entities = remoteMovies.map { dto ->
                        dto.toEntity(
                            isTrending = false,
                            isNowPlaying = true,
                            isBookmarked = bookmarkedIds.contains(dto.id)
                        )
                    }

                    dao.insertMovies(entities)
                }
            } catch (e: Exception) {
                emit(Resource.Error("Could not refresh data: ${e.localizedMessage}", localData.map { it.toDomain() }))
            }
        }

        val updatedLocalData = dao.getNowPlayingMovies().first()
        emit(Resource.Success(updatedLocalData.map { it.toDomain() }))

        emit(Resource.Loading(false))
    }

    /**
     * Fetches the list of bookmarked movies from the local database.
     *
     * @return A [Flow] of a list of bookmarked [Movie]s.
     */
    override fun getBookmarkedMovies(): Flow<List<Movie>> {
        return dao.getBookmarkedMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Fetches a movie by its ID.
     *
     * It first tries to find the movie in the local database. If not found, it fetches the movie
     * details from the remote API and caches it in the local database.
     *
     * @param id The ID of the movie to fetch.
     * @return A [Resource] containing the [Movie].
     */
    override suspend fun getMovieById(id: Int): Resource<Movie> {
        return try {
            val localMovie = dao.getMovieById(id)

            if (localMovie != null) {
                Resource.Success(localMovie.toDomain())
            } else {
                val remoteMovieDto = api.getMovieDetails(id)

                val newEntity = remoteMovieDto.toEntity(
                    isTrending = false,
                    isNowPlaying = false,
                    isBookmarked = false
                )

                dao.insertSingleMovie(newEntity)

                Resource.Success(newEntity.toDomain())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Could not load movie details. Check internet connection.")
        }
    }

    /**
     * Toggles the bookmark status of a movie.
     *
     * @param id The ID of the movie to update.
     * @param isBookmarked The new bookmark status.
     */
    override suspend fun toggleBookmark(id: Int, isBookmarked: Boolean) {
        dao.updateBookmark(id, isBookmarked)
    }

    /**
     * Searches for movies based on a query string.
     *
     * It first tries to fetch search results from the remote API. If the remote search fails,
     * it attempts to search for movies in the local database.
     *
     * @param query The search query.
     * @return A [Flow] of a list of [Movie]s matching the query.
     */
    override suspend fun searchMovies(query: String): Flow<List<Movie>> = flow {
        try {
            val response = api.searchMovies(query)
            val movies = response.results.map { dto ->
                val isBookmarked = dao.getMovieById(dto.id)?.isBookmarked ?: false
                dto.toEntity(isBookmarked = isBookmarked).toDomain()
            }
            emit(movies)
        } catch (_: Exception) {

            val localResults = dao.searchMovies(query).first()

            if (localResults.isNotEmpty()) {
                emit(localResults.map { it.toDomain() })
            } else {
                emit(emptyList())
            }
        }
    }
}
