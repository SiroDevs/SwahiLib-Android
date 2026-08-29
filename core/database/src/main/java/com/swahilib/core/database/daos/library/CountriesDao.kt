package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CountryEntity>)

    @Query("DELETE FROM countries")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<CountryEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("SELECT * FROM countries ORDER BY continent ASC, orderIndex ASC")
    fun getAll(): Flow<List<CountryEntity>>

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun count(): Int
}
