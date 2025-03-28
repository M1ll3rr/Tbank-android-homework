package com.example.myfirstapp.data

import com.example.myfirstapp.library.Months
import com.example.myfirstapp.library.Newspaper

object NewspaperShopData {
    val items = mutableListOf(
        Newspaper(207, "The New York Times", 12346, Months.FEBRUARY),
        Newspaper(208, "The Washington Post", 67891, Months.MARCH),
        Newspaper(209, "Панорама города", 54322, Months.APRIL)
    )
}