package com.example.myfirstapp.shops

import com.example.myfirstapp.library.Disk

class DiskShop : Shop<Disk> {
    override val typeName = "Дисковый"
    override val itemList = mutableListOf(
        Disk(310, "Титаник", "DVD"),
        Disk (311, "Пираты Карибского моря", "DVD"),
        Disk(312, "DAO", "CD")
    )
    override fun sell(choice: Int): Disk {
        val selectedItem = itemList[choice]
        itemList.removeAt(choice)
        return selectedItem
    }
}