package com.example.myfirstapp.library

class Digitalizator<T : Digitizable> {
    fun digitize(item: T, diskType: String = "CD"): Disk {
        val disk = Disk(
            id = "1${item.id}".toInt(),
            name = "Цифровая версия ${item.name}",
            diskType = diskType
        )
        println("${item.typeName} c id ${item.id} записана на $diskType диск c id ${disk.id}")
        return disk
    }
}