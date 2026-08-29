package com.swahilib.core.data.repos.content

import android.util.Log
import com.swahilib.core.common.entity.LibraryDisplayItem
import com.swahilib.core.common.library.LibraryKeys
import com.swahilib.core.data.helpers.toDisplayItem
import com.swahilib.core.database.daos.library.CapsDao
import com.swahilib.core.database.daos.library.CountriesDao
import com.swahilib.core.database.daos.library.FamilyDao
import com.swahilib.core.database.daos.library.FishDao
import com.swahilib.core.database.daos.library.GreetingsDao
import com.swahilib.core.database.daos.library.InsectsDao
import com.swahilib.core.database.daos.library.KidGamesDao
import com.swahilib.core.database.daos.library.PunctuationDao
import com.swahilib.core.database.daos.library.SeasDao
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
    private val capsDao: CapsDao,
    private val countriesDao: CountriesDao,
    private val familyDao: FamilyDao,
    private val fishDao: FishDao,
    private val greetingsDao: GreetingsDao,
    private val insectsDao: InsectsDao,
    private val kidGamesDao: KidGamesDao,
    private val punctuationDao: PunctuationDao,
    private val seasDao: SeasDao,
    private val api: KamusiApi,
) {
    suspend fun fetchRemoteData(endpoint: KamusiApi.Endpoint): Result<Int> =
        withContext(Dispatchers.IO) {
            runCatching {
                val collectionKey = endpoint.libraryCollectionKey
                    ?: error("${endpoint.name} is not a Library endpoint")
                val raw = api.fetchRawJson(endpoint) ?: return@runCatching 0

                val count = when (collectionKey) {
                    LibraryKeys.CAPS -> LibraryMapper.mapCaps(raw)
                        .also { capsDao.replaceAll(it) }.size

                    LibraryKeys.COUNTRIES -> LibraryMapper.mapCountries(raw)
                        .also { countriesDao.replaceAll(it) }.size

                    LibraryKeys.FAMILY -> LibraryMapper.mapFamily(raw)
                        .also { familyDao.replaceAll(it) }.size

                    LibraryKeys.FISH -> LibraryMapper.mapFish(raw)
                        .also { fishDao.replaceAll(it) }.size

                    LibraryKeys.GREETING -> LibraryMapper.mapGreetings(raw)
                        .also { greetingsDao.replaceAll(it) }.size

                    LibraryKeys.INSECTS -> LibraryMapper.mapInsects(raw)
                        .also { insectsDao.replaceAll(it) }.size

                    LibraryKeys.KIDGAMES ->
                        LibraryMapper.mapKidGames(raw).also { kidGamesDao.replaceAll(it) }.size

                    LibraryKeys.PUNCTUATION -> {
                        val (parents, usageByRid) = LibraryMapper.mapPunctuation(raw)
                        punctuationDao.replaceAll(parents, usageByRid)
                        parents.size
                    }

                    LibraryKeys.SEAS -> LibraryMapper.mapSeas(raw)
                        .also { seasDao.replaceAll(it) }.size

                    else -> 0
                }
                Log.d(TAG, "✅ $count library/$collectionKey items seeded")
                count
            }.onFailure { Log.e(TAG, "❌ fetchRemoteData failed: ${it.message}", it) }
        }

    /** Local cache for one collection, mapped to the common [LibraryDisplayItem] the UI consumes. */
    fun displayItemsFor(collectionKey: String): Flow<List<LibraryDisplayItem>> =
        when (collectionKey) {
            LibraryKeys.CAPS -> capsDao.getAll().map { it.map(CapEntity::toDisplayItem) }
            LibraryKeys.COUNTRIES -> countriesDao.getAll()
                .map { it.map(CountryEntity::toDisplayItem) }

            LibraryKeys.FAMILY -> familyDao.getAll().map { it.map(FamilyEntity::toDisplayItem) }
            LibraryKeys.FISH -> fishDao.getAll().map { it.map(FishEntity::toDisplayItem) }
            LibraryKeys.GREETING -> greetingsDao.getAll()
                .map { it.map(GreetingEntity::toDisplayItem) }

            LibraryKeys.INSECTS -> insectsDao.getAll().map { it.map(InsectEntity::toDisplayItem) }
            LibraryKeys.SEAS -> seasDao.getAll().map { it.map(SeasEntity::toDisplayItem) }
            LibraryKeys.KIDGAMES ->
                kidGamesDao.getAll().map { it.map(KidGameEntity::toDisplayItem) }

            LibraryKeys.PUNCTUATION -> punctuationDao.getAllWithUsage()
                .map { it.map(PunctuationWithUsage::toDisplayItem) }

            else -> emptyFlow()
        }

    suspend fun hasLocalData(collectionKey: String): Boolean = withContext(Dispatchers.IO) {
        when (collectionKey) {
            LibraryKeys.CAPS -> capsDao.count()
            LibraryKeys.COUNTRIES -> countriesDao.count()
            LibraryKeys.FAMILY -> familyDao.count()
            LibraryKeys.FISH -> fishDao.count()
            LibraryKeys.GREETING -> greetingsDao.count()
            LibraryKeys.INSECTS -> insectsDao.count()
            LibraryKeys.KIDGAMES -> kidGamesDao.count()
            LibraryKeys.PUNCTUATION -> punctuationDao.count()
            LibraryKeys.SEAS -> seasDao.count()
            else -> 0
        } > 0
    }

    companion object {
        private const val TAG = "LibraryRepo"
    }
}
