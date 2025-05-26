package com.swahilib.core.utils

fun refineTitle(textTitle: String): String {
    return textTitle.replace("''", "'")
}

fun refineContent(contentText: String): String {
    return contentText.replace("''", "'").replace("#", " ")
}

fun songItemTitle(number: Int, title: String): String {
    return if (number != 0) {
        "$number. ${refineTitle(title)}"
    } else {
        refineTitle(title)
    }
}

fun getSongVersesX(songContent: String): List<String> {
    return songContent.split("#").map { it.trim() }.filter { it.isNotEmpty() }
}

fun getSongVerses(songContent: String): List<String> {
    return songContent.split("##").map { it.replace("#", "\n") }
}

fun songCopyString(title: String, content: String): String {
    return "$title\n\n$content"
}

fun bookCountString(title: String, count: Int): String {
    return "$title ($count)"
}

fun lyricsString(lyrics: String): String {
    return lyrics.replace("#", "\n").replace("''", "'")
}

fun songViewerTitle(number: Int, title: String, alias: String): String {
    var songTitle = "$number. ${refineTitle(title)}"

    if (alias.length > 2 && title != alias) {
        songTitle += " (${refineTitle(alias)})"
    }

    return songTitle
}

fun songShareString(title: String, content: String): String {
    return "$title\n\n$content\n\nvia #SwahiLib https://songlib.vercel.app"
}

fun verseOfString(number: String, count: Int): String {
    return "VERSE $number of $count"
}