package com.swahilib.core.common.utils

fun refineTitle(textTitle: String): String {
    return textTitle.replace("''", "'")
}

fun cleanMeaning(meaning: String?): String {
    return (meaning ?: "")
        .replace("\\", "")
        .replace("\"", "")
        .replace(",", ", ")
        .replace("  ", " ")
}
