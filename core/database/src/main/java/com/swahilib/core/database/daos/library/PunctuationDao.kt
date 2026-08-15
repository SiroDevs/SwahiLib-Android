package com.swahilib.core.database.daos.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.swahilib.core.database.entities.library.PunctuationEntity
import com.swahilib.core.database.entities.library.PunctuationUsageEntity
import com.swahilib.core.database.entities.library.PunctuationWithUsage
import kotlinx.coroutines.flow.Flow

@Dao
interface PunctuationDao {
    /** Returns the generated row ids, in the same order as [items], so usage rows can attach to them. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PunctuationEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(items: List<PunctuationUsageEntity>)

    /** `punctuation_usage` rows cascade-delete via the FK, so this alone clears both tables. */
    @Query("DELETE FROM punctuation")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM punctuation")
    suspend fun count(): Int

    @Transaction
    @Query("SELECT * FROM punctuation ORDER BY orderIndex ASC")
    fun getAllWithUsage(): Flow<List<PunctuationWithUsage>>

    /**
     * Replaces the whole collection: wipes both tables, inserts the parent rows, then attaches
     * each parent's usage examples using the freshly generated ids.
     */
    @Transaction
    suspend fun replaceAll(parents: List<PunctuationEntity>, usageByRid: Map<String, List<PunctuationUsageEntity>>) {
        deleteAll()
        val generatedIds = insertAll(parents)
        val usageToInsert = mutableListOf<PunctuationUsageEntity>()
        parents.forEachIndexed { index, parent ->
            val generatedId = generatedIds[index]
            usageByRid[parent.rid]?.forEach { usage ->
                usageToInsert += usage.copy(punctuationId = generatedId)
            }
        }
        if (usageToInsert.isNotEmpty()) insertUsage(usageToInsert)
    }
}
