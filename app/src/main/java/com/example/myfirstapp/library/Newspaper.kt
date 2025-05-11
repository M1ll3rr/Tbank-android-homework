package com.example.myfirstapp.library

import com.example.myfirstapp.data.ItemTypes
import com.example.myfirstapp.data.Months

class Newspaper(
    id: Int,
    name: String,
    val numOfPub: Int,
    val monthOfPub: Months,
    access: Boolean = true
) : LibraryItem(id, name, access) {
    override val itemType: ItemTypes = ItemTypes.NEWSPAPER
}
