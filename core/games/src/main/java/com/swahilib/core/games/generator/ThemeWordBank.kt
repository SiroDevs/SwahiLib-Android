package com.swahilib.core.games.generator

import com.swahilib.core.games.model.SudokuTheme

internal object ThemeWordBank {
    private val banks: Map<SudokuTheme, List<Pair<String, String>>> = mapOf(
        SudokuTheme.ANIMALS to listOf(
            "SIMBA" to "Mfalme wa wanyama",
            "TEMBO" to "Mnyama mkubwa mwenye mkonga",
            "TWIGA" to "Mnyama mwenye shingo ndefu",
            "NYOKA" to "Mnyama atambaaye",
            "PAKA" to "Mnyama wa nyumbani apendaye kula panya",
            "MBWA" to "Rafiki wa mwanadamu, hulinda nyumba",
            "SAMAKI" to "Mnyama aishiye majini",
            "NDEGE" to "Mnyama arukaye angani",
            "SUNGURA" to "Mnyama mdogo mwenye masikio marefu",
            "CHUI" to "Mnyama anayekula nyama ambaye anafanana na paka mkubwa",
        ),
        SudokuTheme.FOOD to listOf(
            "UGALI" to "Chakula kikuu cha unga",
            "WALI" to "Mchele uliopikwa",
            "NYAMA" to "Chakula kitokanacho na mnyama",
            "MBOGA" to "Chakula cha majani",
            "MAHARAGE" to "Aina ya kunde",
            "SAMAKI" to "Chakula kitokanacho na baharini/mtoni",
            "NDIZI" to "Tunda refu la manjano",
            "CHAI" to "Kinywaji cha moto",
            "MAJI" to "Kinywaji muhimu kwa uhai",
            "SUKARI" to "Kitamu kiongezwacho kwenye chai",
        ),
        SudokuTheme.FAMILY to listOf(
            "MAMA" to "Mzazi wa kike",
            "BABA" to "Mzazi wa kiume",
            "KAKA" to "Ndugu wa kiume mkubwa",
            "DADA" to "Ndugu wa kike",
            "BIBI" to "Mama wa mzazi",
            "BABU" to "Baba wa mzazi",
            "MTOTO" to "mtu ambaye hajatimiza umri wa utu uzima",
            "SHANGAZI" to "Dada wa baba",
            "MJOMBA" to "Kaka wa mama",
            "FAMILIA" to "Jamaa ya watu wanaoishi pamoja yenye baba, mama na watoto",
        ),
        SudokuTheme.NATURE to listOf(
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
        SudokuTheme.VERBS to listOf(
            "KULA" to "Kitendo cha kutafuna chakula kinywani na kukimeza",
            "KUNYWA" to "Kitendo cha kutia kitu cha majimaji  kinywani na kukimeza",
            "KUSOMA" to "Kitendo cha kuelewa yaliyoandikwa kwa kutamka au kupitisha macho",
            "KUANDIKA" to "Kitendo cha kuchora herufi kwa kutumia kalamu",
            "KUCHEZA" to "Kitendo cha kufanya jambo la kuchangamsha mwili kwa lengo la kujifurahisha",
            "KULALA" to "Kitendo cha kujinyosha mahali kwa mfano kitandani ili kupata usingizi",
            "KUKIMBIA" to "Kitendo cha kwenda mbio, enda kwa kasi",
            "KUOGELEA" to "Kitendo cha kuelea/kutembea majini",
            "KUIMBA" to "Kitendo cha kutamka maneno kwa kufuata mahadhi fulani kwa sauti",
            "KUPIKA" to "Kitendo cha kuandaa chakula",
        ),
        SudokuTheme.NUMBERS to listOf(
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

    fun wordsFor(theme: SudokuTheme): List<Pair<String, String>> = banks[theme].orEmpty()
}
