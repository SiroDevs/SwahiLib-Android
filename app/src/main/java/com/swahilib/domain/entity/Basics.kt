package com.swahilib.domain.entity

data class Selectable<T>(
    val data: T,
    val isSelected: Boolean = false
)
