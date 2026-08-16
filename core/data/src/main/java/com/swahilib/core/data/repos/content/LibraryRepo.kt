/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.data.repos.content

import android.util.Log
import com.swahilib.core.common.entity.LibraryDetailField
import com.swahilib.core.common.entity.LibraryDisplayItem
import com.swahilib.core.common.library.LibraryKeys
import com.swahilib.core.database.daos.SeasDao
import com.swahilib.core.database.daos.FamilyDao
import com.swahilib.core.database.daos.library.CapsDao
import com.swahilib.core.database.daos.KidGamesDao
import com.swahilib.core.database.daos.CountriesDao
import com.swahilib.core.database.daos.GreetingsDao
import com.swahilib.core.database.daos.FishDao
import com.swahilib.core.database.daos.PunctuationDao
import com.swahilib.core.database.daos.InsectsDao
import com.swahilib.core.database.entities.library.SeasEntity
import com.swahilib.core.database.entities.library.FamilyEntity
import com.swahilib.core.database.entities.library.CapEntity
import com.swahilib.core.database.entities.library.KidGameEntity
import com.swahilib.core.database.entities.library.CountryEntity
import com.swahilib.core.database.entities.library.GreetingEntity
import com.swahilib.core.database.entities.library.FishEntity
import com.swahilib.core.database.entities.library.PunctuationWithUsage
import com.swahilib.core.database.entities.library.InsectEntity
import com.swahilib.core.network.api.KamusiApi
import com.swahilib.core.network.mapper.LibraryMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepo @Inject constructor(
    private val familyDao: FamilyDao,
    private val capsDao: CapsDao,
    private val fishDao: FishDao,
    private val insectsDao: InsectsDao,
    private val seasDao: SeasDao,
    private val kidGamesDao: KidGamesDao,
    private val greetingsDao: GreetingsDao,
    private val countriesDao: CountriesDao,
    private val punctuationDao: PunctuationDao,
    private val api: KamusiApi,
) {
    /** Fetches + caches one collection, routing to its typed table via the endpoint's collection key. */
    suspend fun fetchRemoteData(endpoint: KamusiApi.Endpoint): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val collectionKey = endpoint.libraryCollectionKey
                ?: error("${endpoint.name} is not a Library endpoint")
            val raw = api.fetchRawJson(endpoint) ?: return@runCatching 0

            val count = when (collectionKey) {
                LibraryKeys.FAMILY -> LibraryMapper.mapFamily(raw).also { familyDao.replaceAll(it) }.size
                LibraryKeys.CAPS -> LibraryMapper.mapCaps(raw).also { capsDao.replaceAll(it) }.size
                LibraryKeys.FISH -> LibraryMapper.mapFish(raw).also { fishDao.replaceAll(it) }.size
                LibraryKeys.INSECTS -> LibraryMapper.mapInsects(raw).also { insectsDao.replaceAll(it) }.size
                LibraryKeys.SEAS -> LibraryMapper.mapSeas(raw).also { seasDao.replaceAll(it) }.size
                LibraryKeys.KIDGAMES ->
                    LibraryMapper.mapKidGames(raw).also { kidGamesDao.replaceAll(it) }.size

                LibraryKeys.GREETING -> LibraryMapper.mapGreetings(raw).also { greetingsDao.replaceAll(it) }.size
                LibraryKeys.NCHI -> LibraryMapper.mapCountries(raw).also { countriesDao.replaceAll(it) }.size
                LibraryKeys.PUNCTUATION -> {
                    val (parents, usageByRid) = LibraryMapper.mapPunctuation(raw)
                    punctuationDao.replaceAll(parents, usageByRid)
                    parents.size
                }

                else -> 0
            }
            Log.d(TAG, "✅ $count library/$collectionKey items seeded")
            count
        }.onFailure { Log.e(TAG, "❌ fetchRemoteData failed: ${it.message}", it) }
    }

    /** Local cache for one collection, mapped to the common [LibraryDisplayItem] the UI consumes. */
    fun displayItemsFor(collectionKey: String): Flow<List<LibraryDisplayItem>> = when (collectionKey) {
        LibraryKeys.FAMILY -> familyDao.getAll().map { it.map(FamilyEntity::toDisplayItem) }
        LibraryKeys.CAPS -> capsDao.getAll().map { it.map(CapEntity::toDisplayItem) }
        LibraryKeys.FISH -> fishDao.getAll().map { it.map(FishEntity::toDisplayItem) }
        LibraryKeys.INSECTS -> insectsDao.getAll().map { it.map(InsectEntity::toDisplayItem) }
        LibraryKeys.SEAS -> seasDao.getAll().map { it.map(SeasEntity::toDisplayItem) }
        LibraryKeys.KIDGAMES ->
            kidGamesDao.getAll().map { it.map(KidGameEntity::toDisplayItem) }

        LibraryKeys.GREETING -> greetingsDao.getAll().map { it.map(GreetingEntity::toDisplayItem) }
        LibraryKeys.NCHI -> countriesDao.getAll().map { it.map(CountryEntity::toDisplayItem) }
        LibraryKeys.PUNCTUATION -> punctuationDao.getAllWithUsage().map { it.map(PunctuationWithUsage::toDisplayItem) }
        else -> emptyFlow()
    }

    suspend fun hasLocalData(collectionKey: String): Boolean = withContext(Dispatchers.IO) {
        when (collectionKey) {
            LibraryKeys.FAMILY -> familyDao.count()
            LibraryKeys.CAPS -> capsDao.count()
            LibraryKeys.FISH -> fishDao.count()
            LibraryKeys.INSECTS -> insectsDao.count()
            LibraryKeys.SEAS -> seasDao.count()
            LibraryKeys.KIDGAMES -> kidGamesDao.count()
            LibraryKeys.GREETING -> greetingsDao.count()
            LibraryKeys.NCHI -> countriesDao.count()
            LibraryKeys.PUNCTUATION -> punctuationDao.count()
            else -> 0
        } > 0
    }

    // ---- entity -> display item mappers ----

    private fun FamilyEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        primaryText = title,
        secondaryText = meaning,
        detailFields = listOfNotNull(meaning?.let { LibraryDetailField("Maana", it) }),
        orderIndex = orderIndex,
    )

    private fun CapEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        primaryText = title,
        secondaryText = meaning,
        detailFields = listOfNotNull(meaning?.let { LibraryDetailField("Maana", it) }),
        orderIndex = orderIndex,
    )

    private fun FishEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        primaryText = title,
        orderIndex = orderIndex,
    )

    private fun InsectEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        groupName = category,
        primaryText = title,
        orderIndex = orderIndex,
    )

    private fun SeasEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        primaryText = title,
        secondaryText = size?.let { "Ukubwa: $it km\u00B2" },
        detailFields = listOfNotNull(
            size?.let { LibraryDetailField("Ukubwa (km\u00B2)", it) },
            depth?.let { LibraryDetailField("Kina (m)", it) },
        ),
        orderIndex = orderIndex,
    )

    private fun KidGameEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        primaryText = title,
        secondaryText = reason,
        detailFields = listOfNotNull(
            meaning?.let { LibraryDetailField("Maelezo", it) },
            reason?.let { LibraryDetailField("Lengo", it) },
        ),
        orderIndex = orderIndex,
    )

    private fun GreetingEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        primaryText = greetings,
        secondaryText = answer?.let { "Kiitikio: $it" },
        detailFields = listOfNotNull(
            answer?.let { LibraryDetailField("Kiitikio", it) },
            person1?.let { LibraryDetailField("Anayesalimia", it) },
            person2?.let { LibraryDetailField("Anayesalimiwa", it) },
            time?.let { LibraryDetailField("Wakati", it) },
        ),
        orderIndex = orderIndex,
    )

    private fun CountryEntity.toDisplayItem() = LibraryDisplayItem(
        id = id,
        groupName = continent,
        primaryText = countries,
        secondaryText = english,
        detailFields = listOfNotNull(
            english?.let { LibraryDetailField("Kiingereza", it) },
            nationality?.let { LibraryDetailField("Utaifa", it) },
            capital?.let { LibraryDetailField("Mji Mkuu", it) },
            language?.let { LibraryDetailField("Lugha", it) },
            currency?.let { LibraryDetailField("Sarafu", if (currCode != null) "$it ($currCode)" else it) },
            code?.let { LibraryDetailField("Kodi ya Countries", it) },
        ),
        orderIndex = orderIndex,
    )

    private fun PunctuationWithUsage.toDisplayItem(): LibraryDisplayItem {
        val fields = usage.sortedBy { it.orderIndex }.flatMapIndexed { index, u ->
            listOfNotNull(
                LibraryDetailField("Matumizi ${index + 1}", u.usage),
                u.example?.let { LibraryDetailField("Mfano ${index + 1}", it) },
            )
        }
        return LibraryDisplayItem(
            id = punctuation.id,
            primaryText = listOf(punctuation.sign, punctuation.title).filter { it.isNotBlank() }.joinToString("  "),
            secondaryText = punctuation.title,
            detailFields = fields,
            orderIndex = punctuation.orderIndex,
        )
    }

    companion object {
        private const val TAG = "LibraryRepo"
    }
}
