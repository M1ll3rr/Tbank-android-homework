package com.example.myfirstapp.shops

import com.example.myfirstapp.data.BookShopData
import com.example.myfirstapp.library.Book

class BookShop : AbstractShop<Book>()  {
    override val typeName = "Книжный"
    override val itemList = BookShopData.items
}