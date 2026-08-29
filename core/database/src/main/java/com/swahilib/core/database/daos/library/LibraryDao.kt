package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.LibraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LibraryItemEntity>)

    @Query("DELETE FROM library_items WHERE collection = :collection")
    suspend fun deleteByCollection(collection: String)

    @Transaction
    suspend fun replaceCollection(collection: String, items: List<LibraryItemEntity>) {
        deleteByCollection(collection)
        insertAll(items)
    }

    @Query("SELECT * FROM library_items WHERE collection = :collection ORDER BY orderIndex ASC")
    fun getByCollection(collection: String): Flow<List<LibraryItemEntity>>

    @Query("SELECT COUNT(*) FROM library_items WHERE collection = :collection")
    suspend fun countByCollection(collection: String): Int

    @Query(
        "SELECT * FROM library_items WHERE primaryText LIKE '%' || :query || '%' " +
            "OR secondaryText LIKE '%' || :query || '%' ORDER BY collection, orderIndex"
    )
    fun search(query: String): Flow<List<LibraryItemEntity>>

    @Query("DELETE FROM library_items")
    suspend fun deleteAll()
}
