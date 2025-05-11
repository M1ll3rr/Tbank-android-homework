package com.example.domain.entities

import com.example.domain.types.ItemTypes

class Book(
    id: Int,
    name: String,
    val author: String,
    val numOfPage: Int,
    access: Boolean = true
) : LibraryItem(id, name, access) {
    override val itemType: ItemTypes = ItemTypes.BOOK
}

