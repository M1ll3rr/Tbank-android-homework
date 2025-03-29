package com.example.myfirstapp.data

import com.example.myfirstapp.library.Book
import com.example.myfirstapp.library.Disk
import com.example.myfirstapp.library.Months
import com.example.myfirstapp.library.Newspaper
import com.example.myfirstapp.library.TypesOfDisk

object LibraryData {
    val items =  mutableListOf(
        Book(101, "1984", "Джордж Оруэлл", 328),
        Book(102, "Убить пересмешника", "Харпер Ли", 281),
        Book(103, "Великий Гэтсби", "Фрэнсис Скотт Фицджеральд", 180, false),
        Book(104, "Гордость и предубеждение", "Джейн Остин", 279),
        Book(105, "Над пропастью во ржи", "Джером Д. Сэлинджер", 224),
        Book(106, "Моби Дик", "Герман Мелвилл", 635),
        Book(107, "Война и мир", "Лев Толстой", 1225, false),
        Book(108, "Хоббит", "Дж. Р. Р. Толкин", 310),
        Newspaper(201, "The New York Times", 12345, Months.JANUARY),
        Newspaper(202, "The Washington Post", 67890, Months.FEBRUARY),
        Newspaper(203, "Панорама города", 54321, Months.MARCH, false),
        Newspaper(204, "Комсомольская прада", 98765, Months.APRIL),
        Newspaper(205, "Телесемь", 11223, Months.MAY),
        Newspaper(206, "The Economist", 44556, Months.JUNE),
        Disk(301, "Вестник", TypesOfDisk.CD),
        Disk(302, "Лесник", TypesOfDisk.CD),
        Disk(303, "Back in Black", TypesOfDisk.CD, false),
        Disk(304, "Abbey Road", TypesOfDisk.CD),
        Disk(305, "Rumours", TypesOfDisk.CD),
        Disk(306, "Nevermind", TypesOfDisk.CD),
        Disk(307, "Матрица", TypesOfDisk.DVD, false),
        Disk(308, "Начало", TypesOfDisk.DVD),
        Disk(309, "Интерстеллар", TypesOfDisk.DVD)
    )
}