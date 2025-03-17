package com.example.myfirstapp

class Disk(
    id: Int,
    name: String,
    val diskType: String,
    access: Boolean = true
) : LibraryObject(id, name, access), HomeTakeable {
    override val typeName = "Диск"

    override fun getInfo() {
        println("$diskType $name доступен: ${if (access) "Да" else "Нет"}")
    }

    override fun getHome() {
        if (access) {
            access = false
            println("$typeName $id взяли домой")
        } else {
            println("$typeName $id недоступен")
        }
    }
}
