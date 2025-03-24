package com.example.myfirstapp.shops

import com.example.myfirstapp.library.Disk
import com.example.myfirstapp.library.TypesOfDisk

class DiskShop : AbstractShop<Disk>() {
    override val typeName = "Дисковый"
    override val itemList = mutableListOf(
        Disk(310, "Титаник", TypesOfDisk.DVD.name),
        Disk (311, "Пираты Карибского моря", TypesOfDisk.DVD.name),
        Disk(312, "DAO", TypesOfDisk.CD.name)
    )
}