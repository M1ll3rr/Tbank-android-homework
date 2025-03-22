package com.example.myfirstapp.shops

import com.example.myfirstapp.library.LibraryObject

interface Shop<T : LibraryObject> {
    val typeName: String
    val itemList: MutableList<T>
    fun sell(choice: Int): T
}