package com.example.myfirstapp.data

import com.example.myfirstapp.library.Disk
import com.example.myfirstapp.library.TypesOfDisk

object DiskShopData {
    val items = mutableListOf(
        Disk(310, "Титаник", TypesOfDisk.DVD),
        Disk (311, "Пираты Карибского моря", TypesOfDisk.DVD),
        Disk(312, "DAO", TypesOfDisk.CD)
    )
}