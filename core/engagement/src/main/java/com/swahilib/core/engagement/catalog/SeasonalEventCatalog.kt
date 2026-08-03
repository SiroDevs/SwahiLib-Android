package com.swahilib.core.engagement.catalog

/**
 * Small fixed-date holiday calendar (Kenya-relevant + Kiswahili-specific).
 * Deliberately excludes movable-date observances like Eid al-Fitr/Eid
 * al-Adha, since they follow the lunar calendar and can't be hardcoded
 * reliably across years without a lookup table that needs yearly upkeep -
 * left as a follow-up rather than guessed at.
 */
data class SeasonalEventDef(
    val id: String,
    val title: String,
    val description: String,
    val month: Int, // 1-12
    val day: Int,
    val windowDays: Int = 1, // event is active on [day, day + windowDays - 1] of `month`
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

    /** The holiday active on this exact (month, day), if any. Windows never cross a month boundary. */
    fun activeHoliday(month: Int, day: Int): SeasonalEventDef? =
        HOLIDAYS.firstOrNull { it.month == month && day >= it.day && day < it.day + it.windowDays }
}
