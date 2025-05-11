package com.example.myfirstapp.library

import com.example.myfirstapp.data.ItemTypes

class Book(
    id: Int,
    name: String,
    val author: String,
    val numOfPage: Int,
    access: Boolean = true
) : LibraryItem(id, name, access) {
    override val itemType: ItemTypes = ItemTypes.BOOK
}

