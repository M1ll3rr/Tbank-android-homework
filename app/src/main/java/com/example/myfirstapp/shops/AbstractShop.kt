package com.example.myfirstapp.shops

import com.example.myfirstapp.library.LibraryObject

abstract class AbstractShop<T : LibraryObject> : Shop<T> {
    override fun sell(choice: Int): T {
        return itemList.removeAt(choice)
    }
}