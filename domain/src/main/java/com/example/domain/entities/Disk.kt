package com.example.domain.entities

import com.example.domain.types.DiskTypes
import com.example.domain.types.ItemTypes

class Disk(
    id: Int,
    name: String,
    val diskType: DiskTypes,
    access: Boolean = true
) : LibraryItem(id, name, access){
    override val itemType: ItemTypes = ItemTypes.DISK
}
