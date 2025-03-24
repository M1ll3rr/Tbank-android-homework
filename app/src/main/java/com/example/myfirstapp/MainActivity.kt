package com.example.myfirstapp

import com.example.myfirstapp.library.Book
import com.example.myfirstapp.library.Digitalizator
import com.example.myfirstapp.library.Digitizable
import com.example.myfirstapp.library.Disk
import com.example.myfirstapp.library.HomeTakeable
import com.example.myfirstapp.library.InsideReadable
import com.example.myfirstapp.library.LibraryObject
import com.example.myfirstapp.library.Months
import com.example.myfirstapp.library.Newspaper
import com.example.myfirstapp.library.TypesOfDisk
import com.example.myfirstapp.shops.BookShop
import com.example.myfirstapp.shops.DiskShop
import com.example.myfirstapp.shops.NewspaperShop
import com.example.myfirstapp.shops.Shop

fun main() {
    val manager = Manager()
    val bookShop = BookShop()
    val newspaperShop = NewspaperShop()
    val diskShop = DiskShop()
    val shopList = mutableListOf(bookShop, newspaperShop, diskShop)

    val bookList = mutableListOf<Book>(
        Book(101, "1984", "Джордж Оруэлл", 328),
        Book(102, "Убить пересмешника", "Харпер Ли", 281),
        Book(103, "Великий Гэтсби", "Фрэнсис Скотт Фицджеральд", 180, false),
        Book(104, "Гордость и предубеждение", "Джейн Остин", 279),
        Book(105, "Над пропастью во ржи", "Джером Д. Сэлинджер", 224),
        Book(106, "Моби Дик", "Герман Мелвилл", 635),
        Book(107, "Война и мир", "Лев Толстой", 1225, false),
        Book(108, "Хоббит", "Дж. Р. Р. Толкин", 310)
    )

    val newspaperList = mutableListOf<Newspaper>(
        Newspaper(201, "The New York Times", 12345, Months.JANUARY.ruName),
        Newspaper(202, "The Washington Post", 67890, Months.FEBRUARY.ruName),
        Newspaper(203, "Панорама города", 54321, Months.MARCH.ruName, false),
        Newspaper(204, "Комсомольская прада", 98765, Months.APRIL.ruName),
        Newspaper(205, "Телесемь", 11223, Months.MAY.ruName),
        Newspaper(206, "The Economist", 44556, Months.JUNE.ruName)
    )

    val diskList = mutableListOf<Disk>(
        Disk(301, "Вестник", TypesOfDisk.CD.name),
        Disk(302, "Лесник", TypesOfDisk.CD.name),
        Disk(303, "Back in Black", TypesOfDisk.CD.name, false),
        Disk(304, "Abbey Road", TypesOfDisk.CD.name),
        Disk(305, "Rumours", TypesOfDisk.CD.name),
        Disk(306, "Nevermind", TypesOfDisk.CD.name),
        Disk(307, "Матрица", TypesOfDisk.DVD.name, false),
        Disk(308, "Начало", TypesOfDisk.DVD.name),
        Disk(309, "Интерстеллар", TypesOfDisk.DVD.name)
    )

    println("Добро пожаловать в библиотеку, что вы хотите сделать?")

    while (true) {
        println("1. Показать книги")
        println("2. Показать газеты")
        println("3. Показать диски")
        println("4. Вызвать менеджера")
        println("5. Выполнить оцифровку и запись на диск")
        println("0. Выйти")
        val type = readlnOrNull()?.toIntOrNull()

        when (type) {
            0 -> return
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
                val newItem  = buyNewItem(manager, shopList) ?: continue
                when (newItem) {
                    is Book -> bookList.add(newItem)
                    is Newspaper -> newspaperList.add(newItem)
                    is Disk -> diskList.add(newItem)
                }
                println("${newItem.typeName} ${newItem.name} с id ${newItem.id} теперь есть в библиотеке")
            }
            5 -> {
                val newDisk = getDigitalItem(bookList + newspaperList) ?: continue
                diskList.add(newDisk)
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

fun showShopItems(shopList: List<Shop<out LibraryObject>>) {
    for (index in shopList.indices) {
        println("${index + 1}: ${shopList[index].typeName}")
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
        println("0. Вернуться в начало")

        val action = readlnOrNull()?.toIntOrNull()

        when (action) {
            0 -> return
            1 -> if (item is HomeTakeable) item.getHome()
            else println("${item.typeName} нельзя брать домой!")
            2 -> if (item is InsideReadable) item.readInside()
            else println("${item.typeName} нельзя брать в читальный зал!")
            3 -> item.getInfo()
            4 -> item.returnItem()
            else -> {
                println("Неверный выбор, попробуйте еще раз.")
                continue
            }
        }

    }
}


fun getBuyChoice(shop: Shop<out LibraryObject>) : Int? {

    while (true) {
        println("Выберите порядковый номер предмета для покупки:")
        showLibraryObjects(shop.itemList)
        println("0. Вернуться в начало")

        val choice = readlnOrNull()?.toIntOrNull()

        return when {
            choice == 0 -> null
            choice != null && choice > 0 && choice <= shop.itemList.size -> {
                choice - 1
            }
            else -> {
                println("Неверный выбор, попробуйте еще раз.")
                continue
            }
        }
    }
}

fun getShopChoice(shopList: List<Shop<out LibraryObject>>) : Shop<out LibraryObject>? {
    while (true) {
        println("В какой магазин сходить?")
        showShopItems(shopList)
        println("0. Вернуться в начало")

        val choice = readlnOrNull()?.toIntOrNull()

        return when {
            choice == 0 -> null
            choice != null && choice > 0 && choice <= shopList.size -> {
                shopList[choice - 1]
            }
            else -> {
                println("Неверный выбор, попробуйте еще раз.")
                continue
            }
        }
    }
}

fun buyNewItem(manger: Manager, shopList: List<Shop<out LibraryObject>>) : LibraryObject? {
    val shop = getShopChoice(shopList) ?: return null
    val choice = getBuyChoice(shop) ?: return null
    return manger.buy(shop, choice)
}

fun getDigitalItem(objectList: List<LibraryObject>): Disk? {
    while (true) {
        println("Что вы хотите оцифровать?")
        val uniqueTypes = objectList.distinctBy{it::class}.filter{it is Digitizable}
        for (index in uniqueTypes.indices) {
            println("${index + 1}: ${uniqueTypes[index].typeName}")
        }
        println("0. Вернуться в начало")

        val choice = readlnOrNull()?.toIntOrNull()

        when {
            choice == 0 -> return null
            choice != null && choice > 0 && choice <= uniqueTypes.size -> {
                val selectedType = uniqueTypes[choice - 1]::class
                val filteredList = when (selectedType) {
                    Book::class -> typeFilter<Book>(objectList)
                    Newspaper::class -> typeFilter<Newspaper>(objectList)
                    else -> return null
                }

                while (true) {
                    println("Выберите предмет для оцифровки:")
                    showLibraryObjects(filteredList)
                    println("0. Вернуться в начало")

                    val itemChoice = readlnOrNull()?.toIntOrNull()

                    when {
                        itemChoice == 0 -> return null
                        itemChoice != null && itemChoice > 0 && itemChoice <= filteredList.size -> {
                            val selectedItem = filteredList[itemChoice - 1]
                            return Digitalizator<Digitizable>().digitize(selectedItem)
                        }
                        else -> {
                            println("Неверный выбор, попробуйте еще раз.")
                        }
                    }
                }
            }
            else -> {
                println("Неверный выбор, попробуйте еще раз.")
                continue
            }
        }
    }
}


inline fun <reified T> typeFilter(itemList: List<Any>): List<T> {
    return itemList.filterIsInstance<T>()
}