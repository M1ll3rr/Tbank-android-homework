package com.example.myfirstapp.shops

import com.example.myfirstapp.library.Book

class BookShop : Shop<Book> {
    override val typeName = "Книжный"
    override val itemList = mutableListOf(
        Book(109, "Преступление и наказание", "Фёдор Достоевский", 574),
        Book(110, "Мастер и Маргарита", "Михаил Булгаков", 384),
        Book(111, "Атлант расправил плечи", "Айн Рэнд", 1168)
    )

    override fun sell(choice: Int): Book {
        val selectedItem = itemList[choice]
        itemList.removeAt(choice)
        return selectedItem
    }
}