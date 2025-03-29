package com.example.myfirstapp.shops

import com.example.myfirstapp.library.LibraryItem

class Manager {
    fun buy(shop: Shop<out LibraryItem>, choice: Int) = shop.sell(choice)
}