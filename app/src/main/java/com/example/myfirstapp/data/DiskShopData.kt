package com.example.myfirstapp.data

import com.example.myfirstapp.library.Disk

object DiskShopData {
    val items = mutableListOf(
        Disk(310, "Титаник", DiskTypes.DVD),
        Disk (311, "Пираты Карибского моря", DiskTypes.DVD),
        Disk(312, "DAO", DiskTypes.CD)
    )
}