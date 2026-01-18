package com.example.cinemavault.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a movie in the local database.
 *
 * @property id The unique identifier of the movie.
 * @property title The title of the movie.
 * @property overview A brief summary of the movie.
 * @property posterPath The path to the movie's poster image.
 * @property backdropPath The path to the movie's backdrop image.
 * @property releaseDate The release date of the movie.
 * @property voteAverage The average vote score of the movie.
 * @property isBookmarked Whether the movie is bookmarked by the user.
 * @property isTrending Whether the movie is currently trending.
 * @property isNowPlaying Whether the movie is currently playing in theaters.
 * @property lastUpdated The timestamp of when the movie data was last updated.
 */
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val title: String,

    val overview: String,

    @ColumnInfo(name = "poster_path")
    val posterPath: String?,

    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,

    @ColumnInfo(name = "release_date")
    val releaseDate: String?,

    @ColumnInfo(name = "vote_average")
    val voteAverage: Double,
    
    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean = false,
    
    @ColumnInfo(name = "is_trending")
    val isTrending: Boolean = false,
    
    @ColumnInfo(name = "is_now_playing")
    val isNowPlaying: Boolean = false,
    
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)
