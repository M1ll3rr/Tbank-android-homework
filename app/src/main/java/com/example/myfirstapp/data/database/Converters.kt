package com.example.myfirstapp.data.database

import androidx.room.TypeConverter
import com.example.myfirstapp.data.DiskTypes
import com.example.myfirstapp.data.ItemTypes
import com.example.myfirstapp.data.Months
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromItemTypes(itemType: ItemTypes): Int {
        return itemType.ordinal
    }

    @TypeConverter
    fun toItemTypes(ordinal: Int): ItemTypes {
        return ItemTypes.entries[ordinal]
    }

    @TypeConverter
    fun fromMonths(month: Months?): Int? {
        return month?.ordinal
    }

    @TypeConverter
    fun toMonths(ordinal: Int?): Months? {
        return ordinal?.let { Months.entries[it] }
    }

    @TypeConverter
    fun fromDiskTypes(diskType: DiskTypes?): Int? {
        return diskType?.ordinal
    }

    @TypeConverter
    fun toDiskTypes(ordinal: Int?): DiskTypes? {
        return ordinal?.let { DiskTypes.entries[it] }
    }
}