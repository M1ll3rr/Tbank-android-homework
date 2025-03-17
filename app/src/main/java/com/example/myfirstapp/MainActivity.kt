package com.example.myfirstapp

fun main() {
    val bookList = mutableListOf<Book>(
        Book(1, "1984", "Джордж Оруэлл", 328),
        Book(2, "Убить пересмешника", "Харпер Ли", 281),
        Book(3, "Великий Гэтсби", "Фрэнсис Скотт Фицджеральд", 180, false),
        Book(4, "Гордость и предубеждение", "Джейн Остин", 279),
        Book(5, "Над пропастью во ржи", "Джером Д. Сэлинджер", 224),
        Book(6, "Моби Дик", "Герман Мелвилл", 635),
        Book(7, "Война и мир", "Лев Толстой", 1225, false),
        Book(8, "Хоббит", "Дж. Р. Р. Толкин", 310)
    )

    val newspaperList = mutableListOf<Newspaper>(
        Newspaper(9, "The New York Times", 12345),
        Newspaper(10, "The Washington Post", 67890),
        Newspaper(11, "Панорама города", 54321, false),
        Newspaper(12, "Комсомольская прада", 98765),
        Newspaper(13, "Телесемь", 11223),
        Newspaper(14, "The Economist", 44556)
    )

    val diskList = mutableListOf<Disk>(
        Disk(15, "Вестник", "CD"),
        Disk(16, "Лесник", "CD"),
        Disk(17, "Back in Black", "CD", false),
        Disk(18, "Abbey Road", "CD"),
        Disk(19, "Rumours", "CD"),
        Disk(20, "Nevermind", "CD"),
        Disk(21, "Матрица", "DVD", false),
        Disk(22, "Начало", "DVD"),
        Disk(23, "Интерстеллар", "DVD")
    )

    println("Добро пожаловать в библиотеку, что вы хотите сделать?")

    while (true) {
        println("1. Показать книги")
        println("2. Показать газеты")
        println("3. Показать диски")
        println("4. Выйти")
        val type = readlnOrNull()?.toIntOrNull()

        when (type) {
            1 -> {
                showLibraryObjects(bookList)
                handleItems(bookList)
            }
            2 -> {
                showLibraryObjects(newspaperList)
                handleItems(newspaperList)
            }
            3 -> {
                showLibraryObjects(diskList)
                handleItems(diskList)
            }
            4 -> {
                return
            }
            else -> {
                println("Неверный выбор, попробуйте еще раз.")
                continue
            }
        }
    }
}

fun showLibraryObjects(objectList: List<LibraryObject>) {
    for (index in objectList.indices) {
        println("${index + 1}: ${objectList[index]}")
    }
}

fun handleItems(objectList: List<LibraryObject>) {
    while (true) {
        println("Чтобы выбрать предмет, введите его порядковый номер. Для возврата введите 0")

        val choice = readlnOrNull()?.toIntOrNull()

        when {
            choice == 0 -> return
            choice != null && choice > 0 && choice <= objectList.size -> {
                val selectedItem = objectList[choice - 1]
                handleItemActions(selectedItem)
                return
            }
            else -> {
                println("Неверный выбор, попробуйте еще раз.")
                continue
            }
        }
    }
}

fun handleItemActions(item: LibraryObject) {
    while (true) {
        println("Выберите действие:")
        println("1. Взять домой")
        println("2. Читать в читальном зале")
        println("3. Показать подробную информацию")
        println("4. Вернуть")
        println("5. Вернуться к выбору предметов")

        val action = readlnOrNull()?.toIntOrNull()

        when (action) {
            1 -> if (item is HomeTakeable) item.getHome()
            else println("${item.typeName} нельзя брать домой!")
            2 -> if (item is InsideReadable) item.readInside()
            else println("${item.typeName} нельзя брать в читальный зал!")
            3 -> item.getInfo()
            4 -> item.returnItem()
            5 -> return
            else -> {
                println("Неверный выбор, попробуйте еще раз.")
                continue
            }
        }

    }
}