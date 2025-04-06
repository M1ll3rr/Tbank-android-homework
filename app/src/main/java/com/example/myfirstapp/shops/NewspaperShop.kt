package com.example.myfirstapp.shops

import com.example.myfirstapp.data.NewspaperShopData
import com.example.myfirstapp.library.Newspaper

class NewspaperShop : AbstractShop<Newspaper>() {
    override val typeName = "Газетный"
    override val itemList = NewspaperShopData.items
}