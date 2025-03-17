package com.example.myfirstapp

class Book(
    id: Int,
    name: String,
    val author: String,
    val numOfPage: Int,
    access: Boolean = true
) : LibraryObject(id, name, access), InsideReadable, HomeTakeable {
    override val typeName = "Книга"

    override fun getInfo() {
        println("$typeName: $name ($numOfPage стр.) автора: $author с id: $id доступна: ${if (access) "Да" else "Нет"}")
    }

    override fun getHome() {
        if (access) {
            access = false
            println("$typeName $id взята домой")
        } else {
            println("$typeName $id недоступна")
        }
    }

    override fun readInside() {
        if (access) {
            access = false
            println("$typeName '$name' взята в читальный зал")
        } else {
            println("$typeName '$name' недоступна")
        }
    }
}
