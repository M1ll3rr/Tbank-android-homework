package com.example.myfirstapp.shops

import com.example.myfirstapp.library.LibraryItem

abstract class AbstractShop<T : LibraryItem> : Shop<T> {
    override fun sell(choice: Int): T {
        return itemList.removeAt(choice)
    }
}