package com.example.domain.entities

import com.example.domain.types.ItemTypes
import com.example.domain.types.Months

class Newspaper(
    id: Int,
    name: String,
    val numOfPub: Int,
    val monthOfPub: Months,
    access: Boolean = true
) : LibraryItem(id, name, access) {
    override val itemType: ItemTypes = ItemTypes.NEWSPAPER
}
