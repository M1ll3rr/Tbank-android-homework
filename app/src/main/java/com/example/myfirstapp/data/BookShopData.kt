package com.example.myfirstapp.data

import com.example.myfirstapp.library.Book

object BookShopData {
    val items = mutableListOf(
        Book(109, "Преступление и наказание", "Фёдор Достоевский", 574),
        Book(110, "Мастер и Маргарита", "Михаил Булгаков", 384),
        Book(111, "Атлант расправил плечи", "Айн Рэнд", 1168)
    )
}