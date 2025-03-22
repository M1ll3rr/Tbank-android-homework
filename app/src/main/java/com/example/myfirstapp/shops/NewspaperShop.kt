package com.example.myfirstapp.shops

import com.example.myfirstapp.library.Newspaper

class NewspaperShop : Shop<Newspaper> {
    override val typeName = "Газетный"
    override val itemList = mutableListOf(
        Newspaper(207, "The New York Times", 12346, "Февраль"),
        Newspaper(208, "The Washington Post", 67891, "Март"),
        Newspaper(209, "Панорама города", 54322, "Апрель")
    )
    override fun sell(choice: Int): Newspaper {
        val selectedItem = itemList[choice]
        itemList.removeAt(choice)
        return selectedItem
    }
}