package com.swahilib.core.games.generator

import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.WordEntity

private fun firstMeaningOf(meaning: String?, fallback: String?): String? {
    val meanings = cleanMeaning(meaning).split("|")
    val parts = meanings.firstOrNull()?.split(":")
    val maana = parts?.firstOrNull()?.trim() ?: return fallback?.takeIf { it.isNotBlank() }
    return maana.takeIf { it.isNotBlank() } ?: fallback?.takeIf { it.isNotBlank() }
}

fun WordEntity.definitionText(): String? = firstMeaningOf(meaning, english)

fun ProverbEntity.definitionText(): String? = firstMeaningOf(meaning, fallback = null)
