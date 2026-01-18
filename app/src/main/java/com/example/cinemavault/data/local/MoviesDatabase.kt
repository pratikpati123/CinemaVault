package com.example.cinemavault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cinemavault.data.local.dao.MovieDao
import com.example.cinemavault.data.local.entity.MovieEntity

/**
 * The Room database for the application.
 *
 * This database stores the movie data.
 */
@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MoviesDatabase : RoomDatabase() {
    /**
     * Returns the Data Access Object for the movie table.
     *
     * @return The [MovieDao] for this database.
     */
    abstract fun movieDao(): MovieDao
}
