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

abstract class LibraryObject(val id: Int, val name: String, var access: Boolean) {
    private val type: String = when (this) {
        is Book -> "Книга"
        is Newspaper -> "Газета"
        is Disk -> "Диск"
        else -> "Неизвестный тип"
    }

    override fun toString(): String {
        return "$name доступна: ${if (access) "Да" else "Нет"}"
    }

    abstract fun getInfo()

    fun getHome() {
        if (type != "Газета") {
            if (access) {
                access = false
                println("$type $id взяли домой")
            }
            else println("Предмет недоступен")
        }
        else println("Газету нельзя брать домой!")
    }

    fun readInside() {
        if (type != "Диск") {
            if (access) {
                access = false
                println("$type $id взяли в читальный зал")
            }
            else println("Предмет недоступен")
        }
        else println("Диск нельзя брать в читальный зал!")
    }

    fun returnItem() {
        if (!access) {
            access = true
            println("$type $id вернули в библиотеку")
        } else {
            println("$type $id уже в библиотеке")
        }
    }
}


class Book(
    id: Int,
    name: String,
    val author: String,
    val numOfPage: Int,
    access: Boolean = true
    ) : LibraryObject(id, name, access) {
    override fun getInfo() {
        println("Книга: $name ($numOfPage стр.) автора: $author с id: $id доступна: ${if (access) "Да" else "Нет"}")
    }
}

class Newspaper(
    id: Int,
    name: String,
    val numOfPub: Int,
    access: Boolean = true
) : LibraryObject(id, name, access) {
    override fun getInfo() {
        println("Выпуск: $numOfPub газеты $name с id: $id доступен: ${if (access) "Да" else "Нет"}")
    }
}

class Disk(
    id: Int,
    name: String,
    val diskType: String,
    access: Boolean = true
) : LibraryObject(id, name, access) {
    override fun getInfo() {
        println("$diskType $name доступен: ${if (access) "Да" else "Нет"}")
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
            1 -> item.getHome()
            2 -> item.readInside()
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