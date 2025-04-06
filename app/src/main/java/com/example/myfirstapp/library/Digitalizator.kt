package com.example.myfirstapp.library

import com.example.myfirstapp.data.DiskTypes

class Digitalizator<T : Digitizable> {
    fun digitize(item: T, diskType: DiskTypes = DiskTypes.CD): Disk {
        val disk = Disk(
            id = "1${item.id}".toInt(),
            name = "Цифровая версия ${item.name}",
            diskType = diskType
        )
        return disk
    }
}