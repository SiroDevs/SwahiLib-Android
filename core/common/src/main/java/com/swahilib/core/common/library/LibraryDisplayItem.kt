package com.swahilib.core.common.library

data class LibraryDetailField(val label: String, val value: String)

data class LibraryDisplayItem(
    val id: Long,
    val groupName: String? = null,
    val primaryText: String,
    val secondaryText: String? = null,
    val detailFields: List<LibraryDetailField> = emptyList(),
    val orderIndex: Int = 0,
)
