package com.example.attendanceapp.model

data class RowData(
    val index: Int,
    val firstName: String,
    val surname: String,
    val bowling1: Boolean,
    val bowling2: Boolean,
    val quiz1: Boolean,
    val quiz2: Boolean,
    val present: Boolean,
    val note: String
)