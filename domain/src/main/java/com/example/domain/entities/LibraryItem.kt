package com.example.domain.entities

import com.example.domain.types.ItemTypes

abstract class LibraryItem(val id: Int, val name: String, var access: Boolean) {
    abstract val itemType: ItemTypes

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LibraryItem) return false
        return id == other.id && name == other.name && access == other.access && itemType == other.itemType
    }
}