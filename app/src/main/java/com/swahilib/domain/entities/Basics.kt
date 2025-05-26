package com.swahilib.domain.entities

data class Selectable<T>(
    val data: T,
    val isSelected: Boolean = false
)
