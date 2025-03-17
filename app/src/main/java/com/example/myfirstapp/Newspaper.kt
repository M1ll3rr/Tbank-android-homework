package com.example.myfirstapp

class Newspaper(
    id: Int,
    name: String,
    val numOfPub: Int,
    access: Boolean = true
) : LibraryObject(id, name, access), InsideReadable {
    override val typeName = "Газета"

    override fun getInfo() {
        println("Выпуск: $numOfPub газеты $name с id: $id доступен: ${if (access) "Да" else "Нет"}")
    }

    override fun readInside() {
        if (access) {
            access = false
            println("$typeName $id взята в читальный зал")
        } else {
            println("$typeName $id недоступна")
        }
    }
}
