package com.swahilib.feature.history.model

import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.database.entities.content.WordEntity

/** A resolved history row - whichever content type it turned out to be. */
sealed class ContentItem {
    data class Word(val entity: WordEntity) : ContentItem()
    data class Idiom(val entity: IdiomEntity) : ContentItem()
    data class Proverb(val entity: ProverbEntity) : ContentItem()
    data class Saying(val entity: SayingEntity) : ContentItem()
}
