package com.swahilib.core.common.library

object LibraryKeys {
    const val CAPS = "caps"
    const val COUNTRIES = "countries"
    const val FAMILY = "family"
    const val FISH = "fish"
    const val GREETING = "greetings"
    const val INSECTS = "insects"
    const val KIDGAMES = "kid_games"
    const val PUNCTUATION = "punctuation"
    const val SEAS = "seas"
}

enum class LibraryDisplayMode { GRID, LIST }

data class LibraryConfig(
    val key: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val endpointPath: String,
    val displayMode: LibraryDisplayMode,
    val numberOfGrids: Int = 3,
    val isGrouped: Boolean = false,
    val sideBySide: Boolean = false,
)

object LibraryCatalog {
    val ALL: List<LibraryConfig> = listOf(
        LibraryConfig(
            key = LibraryKeys.CAPS,
            title = "Kofia",
            subtitle = "Aina za kofia za jadi",
            iconName = "Checkroom",
            numberOfGrids = 2,
            endpointPath = "maktaba/${LibraryKeys.CAPS}.json",
            displayMode = LibraryDisplayMode.GRID,
        ),
        LibraryConfig(
            key = LibraryKeys.COUNTRIES,
            title = "Nchi",
            subtitle = "Nchi za dunia",
            iconName = "Public",
            endpointPath = "maktaba/${LibraryKeys.COUNTRIES}.json",
            displayMode = LibraryDisplayMode.LIST,
            isGrouped = true,
            sideBySide = true,
        ),
        LibraryConfig(
            key = LibraryKeys.FAMILY,
            title = "Jamii",
            subtitle = "Majina ya wanajamii",
            iconName = "Groups",
            endpointPath = "maktaba/${LibraryKeys.FAMILY}.json",
            displayMode = LibraryDisplayMode.GRID,
        ),
        LibraryConfig(
            key = LibraryKeys.FISH,
            title = "Samaki",
            subtitle = "Majina ya Samaki",
            iconName = "SetMeal",
            endpointPath = "maktaba/${LibraryKeys.FISH}.json",
            displayMode = LibraryDisplayMode.GRID,
        ),
        LibraryConfig(
            key = LibraryKeys.GREETING,
            title = "Salamu",
            subtitle = "Salamu za kienyeji",
            iconName = "WavingHand",
            endpointPath = "maktaba/${LibraryKeys.GREETING}.json",
            displayMode = LibraryDisplayMode.LIST,
            sideBySide = true,
        ),
        LibraryConfig(
            key = LibraryKeys.INSECTS,
            title = "Wadudu",
            subtitle = "Aina za wadudu",
            iconName = "BugReport",
            endpointPath = "maktaba/${LibraryKeys.INSECTS}.json",
            displayMode = LibraryDisplayMode.GRID,
            isGrouped = true,
        ),
        LibraryConfig(
            key = LibraryKeys.KIDGAMES,
            title = "Michezo ya Watoto",
            subtitle = "Michezo ya asili ya watoto",
            iconName = "Casino",
            endpointPath = "maktaba/${LibraryKeys.KIDGAMES}.json",
            displayMode = LibraryDisplayMode.LIST,
        ),
        LibraryConfig(
            key = LibraryKeys.PUNCTUATION,
            title = "Uakifishaji",
            subtitle = "Alama za uakifishaji",
            iconName = "FormatQuote",
            endpointPath = "maktaba/${LibraryKeys.PUNCTUATION}.json",
            displayMode = LibraryDisplayMode.LIST,
        ),
        LibraryConfig(
            key = LibraryKeys.SEAS,
            title = "Bahari",
            subtitle = "Bahari za dunia",
            iconName = "Water",
            endpointPath = "maktaba/${LibraryKeys.SEAS}.json",
            displayMode = LibraryDisplayMode.LIST,
            sideBySide = true,
        ),
    )

    fun byKey(key: String): LibraryConfig? = ALL.find { it.key == key }
}
