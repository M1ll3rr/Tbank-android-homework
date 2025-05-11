package com.example.myfirstapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfirstapp.data.DiskTypes
import com.example.myfirstapp.data.ItemTypes
import com.example.myfirstapp.data.Months
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