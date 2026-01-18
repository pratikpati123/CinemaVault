package com.example.cinemavault.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cinemavault.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    // --- READ OPERATIONS ---

    @Query("SELECT * FROM movies WHERE is_trending = 1 ORDER BY vote_average DESC")
    fun getTrendingMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE is_now_playing = 1 ORDER BY release_date DESC")
    fun getNowPlayingMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE is_bookmarked = 1")
    fun getBookmarkedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    // --- SEARCH OPERATION ---

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchMovies(query: String): Flow<List<MovieEntity>>


    // --- WRITE OPERATIONS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleMovie(movie: MovieEntity)

    /**
     * Updates only the specific bookmark field.
     * This is efficient and safe—it won't touch the movie title/images.
     */
    @Query("UPDATE movies SET is_bookmarked = :isBookmarked WHERE id = :movieId")
    suspend fun updateBookmark(movieId: Int, isBookmarked: Boolean)

    /**
     * Resets the 'Trending' flag for all movies.
     * Used when refreshing data from API so old movies don't stay 'Trending'.
     */
    @Query("UPDATE movies SET is_trending = 0")
    suspend fun clearTrending()

    /**
     * Resets the 'Now Playing' flag for all movies.
     */
    @Query("UPDATE movies SET is_now_playing = 0")
    suspend fun clearNowPlaying()
}