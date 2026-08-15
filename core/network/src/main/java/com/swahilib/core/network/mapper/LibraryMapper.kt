package com.swahilib.core.network.mapper

import com.swahilib.core.database.entities.library.SeasEntity
import com.swahilib.core.database.entities.library.FamilyEntity
import com.swahilib.core.database.entities.library.CapEntity
import com.swahilib.core.database.entities.library.KidGameEntity
import com.swahilib.core.database.entities.library.CountryEntity
import com.swahilib.core.database.entities.library.GreetingEntity
import com.swahilib.core.database.entities.library.FishEntity
import com.swahilib.core.database.entities.library.PunctuationEntity
import com.swahilib.core.database.entities.library.PunctuationUsageEntity
import com.swahilib.core.database.entities.library.InsectEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

object LibraryMapper {

    private val json = Json { ignoreUnknownKeys = true }

    private fun parse(rawJson: String): JsonElement = json.parseToJsonElement(rawJson)

    private fun JsonElement?.str(): String? =
        (this as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }

    private fun JsonObject.ridOrIndex(index: Int): String =
        this["rid"].str() ?: (index + 1).toString()

    // ---- family: [{rid, title, meaning}] ----
    fun mapFamily(rawJson: String): List<FamilyEntity> =
        parse(rawJson).jsonArray.mapIndexed { index, el ->
            val obj = el.jsonObject
            FamilyEntity(
                rid = obj.ridOrIndex(index),
                title = obj["title"].str().orEmpty(),
                meaning = obj["meaning"].str(),
                orderIndex = index,
            )
        }

    // ---- caps: [{rid, title, meaning}] ----
    fun mapCaps(rawJson: String): List<CapEntity> =
        parse(rawJson).jsonArray.mapIndexed { index, el ->
            val obj = el.jsonObject
            CapEntity(
                rid = obj.ridOrIndex(index),
                title = obj["title"].str().orEmpty(),
                meaning = obj["meaning"].str(),
                orderIndex = index,
            )
        }

    // ---- fish: [{rid, title}] ----
    fun mapFish(rawJson: String): List<FishEntity> =
        parse(rawJson).jsonArray.mapIndexed { index, el ->
            val obj = el.jsonObject
            FishEntity(
                rid = obj.ridOrIndex(index),
                title = obj["title"].str().orEmpty(),
                orderIndex = index,
            )
        }

    // ---- insects: { "Category name": [{rid, title}], ... } ----
    fun mapInsects(rawJson: String): List<InsectEntity> {
        val entries = mutableListOf<InsectEntity>()
        var order = 0
        parse(rawJson).jsonObject.forEach { (category, arr) ->
            arr.jsonArray.forEach { el ->
                val obj = el.jsonObject
                entries += InsectEntity(
                    rid = obj.ridOrIndex(order),
                    category = category,
                    title = obj["title"].str().orEmpty(),
                    orderIndex = order,
                )
                order++
            }
        }
        return entries
    }

    fun mapSeas(rawJson: String): List<SeasEntity> =
        parse(rawJson).jsonArray.mapIndexed { index, el ->
            val obj = el.jsonObject
            SeasEntity(
                rid = obj.ridOrIndex(index),
                title = obj["title"].str().orEmpty(),
                size = obj["size"].str(),
                depth = obj["depth"].str(),
                orderIndex = index,
            )
        }

    // ---- kid_games: [{rid, title, meaning, lengo}] ----
    fun mapKidGames(rawJson: String): List<KidGameEntity> =
        parse(rawJson).jsonArray.mapIndexed { index, el ->
            val obj = el.jsonObject
            KidGameEntity(
                rid = obj.ridOrIndex(index),
                title = obj["title"].str().orEmpty(),
                meaning = obj["meaning"].str(),
                reason = obj["reason"].str(),
                orderIndex = index,
            )
        }

    fun mapGreetings(rawJson: String): List<GreetingEntity> =
        parse(rawJson).jsonArray.mapIndexed { index, el ->
            val obj = el.jsonObject
            GreetingEntity(
                rid = obj.ridOrIndex(index),
                greetings = obj["greetings"].str().orEmpty(),
                answer = obj["answer"].str(),
                person1 = obj["person1"].str(),
                person2 = obj["person2"].str(),
                time = obj["time"].str(),
                orderIndex = index,
            )
        }

    fun mapCountries(rawJson: String): List<CountryEntity> {
        val entries = mutableListOf<CountryEntity>()
        var order = 0
        parse(rawJson).jsonObject.forEach { (continent, arr) ->
            arr.jsonArray.forEach { el ->
                val obj = el.jsonObject
                val language =
                    (obj["language"] as? JsonArray)?.mapNotNull { it.str() }?.joinToString(", ")
                val currencyObj = obj["currency"] as? JsonObject
                entries += CountryEntity(
                    rid = obj.ridOrIndex(order),
                    continent = continent,
                    countries = obj["countries"].str().orEmpty(),
                    english = obj["english"].str(),
                    nationality = obj["nationality"].str(),
                    capital = obj["capital"].str(),
                    language = language,
                    currency = currencyObj?.get("title").str(),
                    currCode = currencyObj?.get("kodi").str(),
                    code = obj["kodi"].str(),
                    orderIndex = order,
                )
                order++
            }
        }
        return entries
    }

    fun mapPunctuation(rawJson: String): Pair<List<PunctuationEntity>, Map<String, List<PunctuationUsageEntity>>> {
        val parents = mutableListOf<PunctuationEntity>()
        val usageByRid = mutableMapOf<String, List<PunctuationUsageEntity>>()

        parse(rawJson).jsonArray.forEachIndexed { index, el ->
            val obj = el.jsonObject
            val rid = obj.ridOrIndex(index)
            parents += PunctuationEntity(
                rid = rid,
                sign = obj["sign"].str().orEmpty(),
                title = obj["title"].str().orEmpty(),
                orderIndex = index,
            )

            val meaningArr = (obj["meaning"] as? JsonArray) ?: JsonArray(emptyList())
            val usages = meaningArr.mapIndexedNotNull { usageIndex, m ->
                val mObj = m.jsonObject
                val usage = mObj["usage"].str() ?: return@mapIndexedNotNull null
                PunctuationUsageEntity(
                    punctuationId = 0,
                    usage = usage,
                    example = mObj["example"].str(),
                    orderIndex = usageIndex,
                )
            }
            if (usages.isNotEmpty()) usageByRid[rid] = usages
        }

        return parents to usageByRid
    }
}
