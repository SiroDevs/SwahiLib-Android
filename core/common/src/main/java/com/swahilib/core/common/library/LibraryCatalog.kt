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

enum class LibraryDisplayMode {
    GRID,
    LIST,
}

data class LibraryCollectionConfig(
    val key: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val endpointPath: String,
    val displayMode: LibraryDisplayMode,
    val isGrouped: Boolean = false,
)

object LibraryCatalog {
    val ALL: List<LibraryCollectionConfig> = listOf(
        LibraryCollectionConfig(
            key = LibraryKeys.CAPS,
            title = "Kofia",
            subtitle = "Aina za kofia za jadi",
            iconName = "Checkroom",
            endpointPath = "maktaba/${LibraryKeys.CAPS}.json",
            displayMode = LibraryDisplayMode.GRID,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.COUNTRIES,
            title = "Nchi",
            subtitle = "Nchi za dunia",
            iconName = "Public",
            endpointPath = "maktaba/${LibraryKeys.COUNTRIES}.json",
            displayMode = LibraryDisplayMode.LIST,
            isGrouped = true,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.FAMILY,
            title = "Family",
            subtitle = "Matitle ya wanafamilia",
            iconName = "Groups",
            endpointPath = "maktaba/${LibraryKeys.FAMILY}.json",
            displayMode = LibraryDisplayMode.GRID,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.FISH,
            title = "Samaki",
            subtitle = "Matitle ya Samaki",
            iconName = "SetMeal",
            endpointPath = "maktaba/${LibraryKeys.FISH}.json",
            displayMode = LibraryDisplayMode.GRID,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.GREETING,
            title = "Salamu",
            subtitle = "Salamu za kienyeji",
            iconName = "WavingHand",
            endpointPath = "maktaba/${LibraryKeys.GREETING}.json",
            displayMode = LibraryDisplayMode.LIST,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.INSECTS,
            title = "Wadudu",
            subtitle = "Aina za wadudu",
            iconName = "BugReport",
            endpointPath = "maktaba/${LibraryKeys.INSECTS}.json",
            displayMode = LibraryDisplayMode.GRID,
            isGrouped = true,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.KIDGAMES,
            title = "Michezo ya Watoto",
            subtitle = "Michezo ya asili ya watoto",
            iconName = "Casino",
            endpointPath = "maktaba/${LibraryKeys.KIDGAMES}.json",
            displayMode = LibraryDisplayMode.LIST,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.PUNCTUATION,
            title = "Uakifishaji",
            subtitle = "Alama za uakifishaji",
            iconName = "FormatQuote",
            endpointPath = "maktaba/${LibraryKeys.PUNCTUATION}.json",
            displayMode = LibraryDisplayMode.LIST,
        ),
        LibraryCollectionConfig(
            key = LibraryKeys.SEAS,
            title = "Bahari",
            subtitle = "Bahari za dunia",
            iconName = "Water",
            endpointPath = "maktaba/${LibraryKeys.SEAS}.json",
            displayMode = LibraryDisplayMode.LIST,
        ),
    )

    fun byKey(key: String): LibraryCollectionConfig? = ALL.find { it.key == key }
}
