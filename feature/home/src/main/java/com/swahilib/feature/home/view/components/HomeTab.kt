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

package com.swahilib.feature.home.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity

sealed class ContentItem {
    data class Word(val entity: WordEntity) : ContentItem()
    data class Idiom(val entity: IdiomEntity) : ContentItem()
    data class Proverb(val entity: ProverbEntity) : ContentItem()
    data class Saying(val entity: SayingEntity) : ContentItem()
}

sealed class HomeTab(var title: String, var icon: ImageVector) {
    object Search : HomeTab("Tafuta", Icons.Default.Search)
    object Likes : HomeTab("Vipendwa", Icons.Default.Favorite)
    object History : HomeTab("Historia", Icons.Default.History)
}

val homeTabs = listOf(
    HomeTab.Search,
    HomeTab.Likes,
    HomeTab.History,
)
