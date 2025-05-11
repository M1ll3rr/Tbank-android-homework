package com.example.myfirstapp.library

import com.example.myfirstapp.data.DiskTypes
import com.example.myfirstapp.data.ItemTypes

class Disk(
    id: Int,
    name: String,
    val diskType: DiskTypes,
    access: Boolean = true
) : LibraryItem(id, name, access){
    override val itemType: ItemTypes = ItemTypes.DISK
}
