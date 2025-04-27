package com.example.myfirstapp.library

import android.content.Context
import com.example.myfirstapp.data.ItemTypes

abstract class LibraryItem(val id: Int, val name: String, var access: Boolean) {
    abstract val itemType: ItemTypes
    val iconId: Int get() = itemType.iconId

    fun getTypeName(context: Context) = itemType.getTypeName(context)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LibraryItem) return false
        return id == other.id && name == other.name && access == other.access && itemType == other.itemType
    }
}