package com.example.data.mappers

import com.example.data.database.LibraryItemEntity
import com.example.domain.entities.Book
import com.example.domain.entities.Disk
import com.example.domain.entities.LibraryItem
import com.example.domain.entities.Newspaper
import com.example.domain.types.ItemTypes

object EntityMapper {
    fun toEntity(item: LibraryItem): LibraryItemEntity {
        return when (item) {
            is Book -> LibraryItemEntity(
                id = item.id,
                name = item.name,
                access = item.access,
                itemType = item.itemType,
                author = item.author,
                numOfPage = item.numOfPage
            )
            is Newspaper -> LibraryItemEntity(
                id = item.id,
                name = item.name,
                access = item.access,
                itemType = item.itemType,
                numOfPub = item.numOfPub,
                monthOfPub = item.monthOfPub
            )
            is Disk -> LibraryItemEntity(
                id = item.id,
                name = item.name,
                access = item.access,
                itemType = item.itemType,
                diskType = item.diskType
            )
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    fun fromEntity(entity: LibraryItemEntity): LibraryItem {
        return when (entity.itemType) {
            ItemTypes.BOOK -> Book(
                id = entity.id,
                name = entity.name,
                author = entity.author!!,
                numOfPage = entity.numOfPage!!,
                access = entity.access
            )
            ItemTypes.NEWSPAPER -> Newspaper(
                id = entity.id,
                name = entity.name,
                numOfPub = entity.numOfPub!!,
                monthOfPub = entity.monthOfPub!!,
                access = entity.access
            )
            ItemTypes.DISK -> Disk(
                id = entity.id,
                name = entity.name,
                diskType = entity.diskType!!,
                access = entity.access
            )
        }
    }
}