package com.example.myfirstapp

import com.example.myfirstapp.library.LibraryObject
import com.example.myfirstapp.shops.Shop

class Manager {
    fun buy(shop: Shop<out LibraryObject>, choice: Int) = shop.sell(choice)
}