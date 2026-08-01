package com.swahilib.core.games.generator

import com.swahilib.core.games.model.WordSearchTheme

/**
 * `WordEntity` has no category/tag column, so themed Word Search puzzles
 * (Animals, Food, Family, Nature, Verbs, Numbers per CLAUDE.md) can't be
 * queried from the dictionary directly today. This is a small curated
 * fallback bank so themed mode still works; RANDOM mode pulls live from
 * `words` instead and needs no bank at all.
 *
 * TODO(Sprint 3+): once WordEntity gets a category column, replace this
 * with a WordDao query and delete the hardcoded lists.
 */
internal object ThemeWordBank {

    private val banks: Map<WordSearchTheme, List<Pair<String, String>>> = mapOf(
        WordSearchTheme.ANIMALS to listOf(
            "SIMBA" to "Mfalme wa wanyama",
            "TEMBO" to "Mnyama mkubwa mwenye pua ndefu",
            "TWIGA" to "Mnyama mwenye shingo ndefu",
            "NYOKA" to "Mnyama atambaaye asiye na miguu",
            "PAKA" to "Mnyama wa nyumbani apendaye samaki",
            "MBWA" to "Rafiki wa mwanadamu, hulinda nyumba",
            "SAMAKI" to "Mnyama aishiye majini",
            "NDEGE" to "Mnyama arukaye angani",
            "SUNGURA" to "Mnyama mdogo mwenye masikio marefu",
            "CHUI" to "Mnyama mwenye madoa, mkali",
        ),
        WordSearchTheme.FOOD to listOf(
            "UGALI" to "Chakula kikuu cha unga",
            "WALI" to "Mchele uliopikwa",
            "NYAMA" to "Chakula kitokanacho na mnyama",
            "MBOGA" to "Chakula cha majani",
            "MAHARAGE" to "Aina ya kunde",
            "SAMAKI" to "Chakula kitokanacho na baharini/mtoni",
            "NDIZI" to "Tunda refu la njano",
            "CHAI" to "Kinywaji cha moto",
            "MAJI" to "Kinywaji muhimu kwa uhai",
            "SUKARI" to "Kitamu kiongezwacho kwenye chai",
        ),
        WordSearchTheme.FAMILY to listOf(
            "MAMA" to "Mzazi wa kike",
            "BABA" to "Mzazi wa kiume",
            "KAKA" to "Ndugu wa kiume mkubwa",
            "DADA" to "Ndugu wa kike",
            "BIBI" to "Mama wa mzazi",
            "BABU" to "Baba wa mzazi",
            "MTOTO" to "Kijana mdogo",
            "SHANGAZI" to "Dada wa baba",
            "MJOMBA" to "Kaka wa mama",
            "FAMILIA" to "Kikundi cha wazazi na watoto",
        ),
        WordSearchTheme.NATURE to listOf(
            "MTI" to "Mmea mkubwa wenye shina",
            "MLIMA" to "Ardhi iliyoinuka juu sana",
            "MTO" to "Maji yatiririkayo",
            "BAHARI" to "Maji mengi yenye chumvi",
            "JUA" to "Nuru ya mchana angani",
            "MWEZI" to "Nuru ya usiku angani",
            "ANGA" to "Nafasi juu ya ardhi",
            "MVUA" to "Maji yaangukayo kutoka mawinguni",
            "UPEPO" to "Hewa inayovuma",
            "MSITU" to "Eneo lenye miti mingi",
        ),
        WordSearchTheme.VERBS to listOf(
            "KULA" to "Kitendo cha kupokea chakula mdomoni",
            "KUNYWA" to "Kitendo cha kupokea kinywaji",
            "KUSOMA" to "Kitendo cha kuelewa maandishi",
            "KUANDIKA" to "Kitendo cha kuweka maneno karatasini",
            "KUCHEZA" to "Kitendo cha kufanya mchezo",
            "KULALA" to "Kitendo cha kupumzika kwa usingizi",
            "KUKIMBIA" to "Kitendo cha kwenda kwa kasi kwa miguu",
            "KUOGELEA" to "Kitendo cha kuelea/kutembea majini",
            "KUIMBA" to "Kitendo cha kutoa wimbo",
            "KUPIKA" to "Kitendo cha kuandaa chakula",
        ),
        WordSearchTheme.NUMBERS to listOf(
            "MOJA" to "Nambari 1",
            "MBILI" to "Nambari 2",
            "TATU" to "Nambari 3",
            "NNE" to "Nambari 4",
            "TANO" to "Nambari 5",
            "SITA" to "Nambari 6",
            "SABA" to "Nambari 7",
            "NANE" to "Nambari 8",
            "TISA" to "Nambari 9",
            "KUMI" to "Nambari 10",
        ),
    )

    fun wordsFor(theme: WordSearchTheme): List<Pair<String, String>> = banks[theme].orEmpty()
}
