package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.types.DiskTypes
import com.example.domain.types.ItemTypes
import com.example.domain.types.Months
import java.util.Date


@Entity(tableName = "library_items")
data class LibraryItemEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val access: Boolean,
    val itemType: ItemTypes,
    val created: Date = Date(),

    val author: String? = null,
    val numOfPage: Int? = null,

    val numOfPub: Int? = null,
    val monthOfPub: Months? = null,

    val diskType: DiskTypes? = null
)