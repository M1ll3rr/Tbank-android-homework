package com.example.myfirstapp.shops

import com.example.myfirstapp.library.LibraryItem

interface Shop<T : LibraryItem> {
    val typeName: String
    val itemList: MutableList<T>
    fun sell(choice: Int): T
}