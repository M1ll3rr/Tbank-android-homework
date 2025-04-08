package com.example.myfirstapp.shops

import com.example.myfirstapp.data.DiskShopData
import com.example.myfirstapp.library.Disk

class DiskShop : AbstractShop<Disk>() {
    override val typeName = "Дисковый"
    override val itemList = DiskShopData.items
}