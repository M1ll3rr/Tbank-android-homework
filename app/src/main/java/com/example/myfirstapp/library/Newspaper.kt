package com.example.myfirstapp.library

import com.example.myfirstapp.R

class Newspaper(
    id: Int,
    name: String,
    val numOfPub: Int,
    val monthOfPub: Months,
    access: Boolean = true
) : LibraryObject(id, name, access), InsideReadable, Digitizable {
    override val typeName = "Газета"
    override val iconId = R.drawable.diskicon

    override fun getInfo() {
        println("Выпуск №$numOfPub $monthOfPub газеты $name с id: $id доступен: ${if (access) "Да" else "Нет"}")
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
