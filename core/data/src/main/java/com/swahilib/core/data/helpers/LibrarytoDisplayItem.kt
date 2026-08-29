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

package com.swahilib.core.data.helpers

import com.swahilib.core.common.entity.LibraryDetailField
import com.swahilib.core.common.entity.LibraryDisplayItem
import com.swahilib.core.database.entities.library.CapEntity
import com.swahilib.core.database.entities.library.CountryEntity
import com.swahilib.core.database.entities.library.FamilyEntity
import com.swahilib.core.database.entities.library.FishEntity
import com.swahilib.core.database.entities.library.GreetingEntity
import com.swahilib.core.database.entities.library.InsectEntity
import com.swahilib.core.database.entities.library.KidGameEntity
import com.swahilib.core.database.entities.library.PunctuationWithUsage
import com.swahilib.core.database.entities.library.SeasEntity

fun FamilyEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    primaryText = title,
    secondaryText = meaning,
    detailFields = listOfNotNull(meaning?.let { LibraryDetailField("Maana", it) }),
    orderIndex = orderIndex,
)

fun CapEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    primaryText = title,
    secondaryText = meaning,
    detailFields = listOfNotNull(meaning?.let { LibraryDetailField("Maana", it) }),
    orderIndex = orderIndex,
)

fun FishEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    primaryText = title,
    orderIndex = orderIndex,
)

fun InsectEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    groupName = category,
    primaryText = title,
    orderIndex = orderIndex,
)

fun SeasEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    primaryText = title,
    secondaryText = size?.let { "Ukubwa: $it km\u00B2" },
    detailFields = listOfNotNull(
        size?.let { LibraryDetailField("Ukubwa (km\u00B2)", it) },
        depth?.let { LibraryDetailField("Kina (m)", it) },
    ),
    orderIndex = orderIndex,
)

fun KidGameEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    primaryText = title,
    secondaryText = reason,
    detailFields = listOfNotNull(
        meaning?.let { LibraryDetailField("Maelezo", it) },
        reason?.let { LibraryDetailField("Lengo", it) },
    ),
    orderIndex = orderIndex,
)

fun GreetingEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    primaryText = greeting,
    secondaryText = answer?.let { "Kiitikio: $it" },
    detailFields = listOfNotNull(
        answer?.let { LibraryDetailField("Kiitikio", it) },
        person1?.let { LibraryDetailField("Anayesalimia", it) },
        person2?.let { LibraryDetailField("Anayesalimiwa", it) },
        time?.let { LibraryDetailField("Wakati", it) },
    ),
    orderIndex = orderIndex,
)

fun CountryEntity.toDisplayItem() = LibraryDisplayItem(
    id = id,
    groupName = continent,
    primaryText = countries,
    secondaryText = english,
    detailFields = listOfNotNull(
        english?.let { LibraryDetailField("Kiingereza", it) },
        nationality?.let { LibraryDetailField("Utaifa", it) },
        capital?.let { LibraryDetailField("Mji Mkuu", it) },
        language?.let { LibraryDetailField("Lugha", it) },
        currency?.let {
            LibraryDetailField(
                "Sarafu",
                if (currCode != null) "$it ($currCode)" else it
            )
        },
        code?.let { LibraryDetailField("Kodi ya Countries", it) },
    ),
    orderIndex = orderIndex,
)

fun PunctuationWithUsage.toDisplayItem(): LibraryDisplayItem {
    val fields = usage.sortedBy { it.orderIndex }.flatMapIndexed { index, u ->
        listOfNotNull(
            LibraryDetailField("Matumizi ${index + 1}", u.usage),
            u.example?.let { LibraryDetailField("Mfano ${index + 1}", it) },
        )
    }
    return LibraryDisplayItem(
        id = punctuation.id,
        primaryText = listOf(punctuation.sign, punctuation.title).filter { it.isNotBlank() }
            .joinToString("  "),
        secondaryText = punctuation.title,
        detailFields = fields,
        orderIndex = punctuation.orderIndex,
    )
}