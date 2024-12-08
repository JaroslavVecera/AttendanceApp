package com.example.attendanceapp.model

enum class SortDirection {
    None,
    Asc,
    Desc;

    fun next(): SortDirection {
        val values = values()
        val nextIndex = (ordinal + 1) % values.size
        return values[nextIndex]
    }
}