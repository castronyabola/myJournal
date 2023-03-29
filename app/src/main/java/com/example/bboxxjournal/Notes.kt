package com.example.bboxxjournal

import android.content.res.ColorStateList

data class Notes(
    val title: String, var time: String, var mood: ColorStateList, var isChecked: Boolean = false
)