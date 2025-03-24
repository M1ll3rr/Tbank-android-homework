package com.example.myfirstapp.shops

import com.example.myfirstapp.library.Months
import com.example.myfirstapp.library.Newspaper

class NewspaperShop : AbstractShop<Newspaper>() {
    override val typeName = "Газетный"
    override val itemList = mutableListOf(
        Newspaper(207, "The New York Times", 12346, Months.FEBRUARY.ruName),
        Newspaper(208, "The Washington Post", 67891, Months.MARCH.ruName),
        Newspaper(209, "Панорама города", 54322, Months.APRIL.ruName)
    )
}