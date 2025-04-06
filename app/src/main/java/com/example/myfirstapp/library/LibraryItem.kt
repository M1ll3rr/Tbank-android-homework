package com.example.myfirstapp.library

import android.content.Context
import com.example.myfirstapp.data.ItemTypes

abstract class LibraryItem(val id: Int, val name: String, var access: Boolean) {
    abstract val itemType: ItemTypes
    val iconId: Int get() = itemType.iconId

    fun getTypeName(context: Context) = itemType.getTypeName(context)
}