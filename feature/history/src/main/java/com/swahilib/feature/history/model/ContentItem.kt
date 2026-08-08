package com.swahilib.feature.history.model

import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity

/** A resolved history row - whichever content type it turned out to be. */
sealed class ContentItem {
    data class Word(val entity: WordEntity) : ContentItem()
    data class Idiom(val entity: IdiomEntity) : ContentItem()
    data class Proverb(val entity: ProverbEntity) : ContentItem()
    data class Saying(val entity: SayingEntity) : ContentItem()
}
