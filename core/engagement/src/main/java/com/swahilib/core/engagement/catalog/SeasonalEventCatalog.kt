package com.swahilib.core.engagement.catalog

data class SeasonalEventDef(
    val id: String,
    val title: String,
    val description: String,
    val month: Int, // 1-12
    val day: Int,
    val windowDays: Int = 1,
)

object SeasonalEventCatalog {

    val HOLIDAYS = listOf(
        SeasonalEventDef(
            id = "new_year",
            title = "Mwaka Mpya",
            description = "Anza mwaka kwa mafanikio ya kujifunza Kiswahili!",
            month = 1, day = 1, windowDays = 3,
        ),
        SeasonalEventDef(
            id = "madaraka_day",
            title = "Siku ya Madaraka",
            description = "Sherehekea Siku ya Madaraka kwa changamoto maalum!",
            month = 6, day = 1,
        ),
        SeasonalEventDef(
            id = "kiswahili_day",
            title = "Siku ya Kiswahili Duniani",
            description = "Sherehekea lugha yetu adhimu ya Kiswahili!",
            month = 7, day = 7, windowDays = 3,
        ),
        SeasonalEventDef(
            id = "mashujaa_day",
            title = "Siku ya Mashujaa",
            description = "Heshimu mashujaa wa taifa kwa kujifunza zaidi!",
            month = 10, day = 20,
        ),
        SeasonalEventDef(
            id = "jamhuri_day",
            title = "Siku ya Jamhuri",
            description = "Sherehekea Uhuru na Jamhuri ya Kenya!",
            month = 12, day = 12,
        ),
        SeasonalEventDef(
            id = "christmas",
            title = "Krismasi",
            description = "Zawadi maalum za msimu wa Krismasi!",
            month = 12, day = 25, windowDays = 3,
        ),
    )

    fun activeHoliday(month: Int, day: Int): SeasonalEventDef? =
        HOLIDAYS.firstOrNull { it.month == month && day >= it.day && day < it.day + it.windowDays }
}
