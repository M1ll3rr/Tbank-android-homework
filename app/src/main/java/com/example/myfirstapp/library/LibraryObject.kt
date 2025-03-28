package com.example.myfirstapp.library

abstract class LibraryObject(val id: Int, val name: String, var access: Boolean) {
    abstract val typeName: String
    abstract val iconId: Int

    override fun toString(): String {
        return "$name доступна: ${if (access) "Да" else "Нет"}"
    }

    abstract fun getInfo()

    fun returnItem() {
        if (!access) {
            access = true
            println("$typeName $id вернули в библиотеку")
        } else {
            println("$typeName $id уже в библиотеке")
        }
    }
}