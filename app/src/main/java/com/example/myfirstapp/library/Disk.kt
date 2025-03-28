package com.example.myfirstapp.library

import com.example.myfirstapp.R

class Disk(
    id: Int,
    name: String,
    val diskType: TypesOfDisk,
    access: Boolean = true
) : LibraryObject(id, name, access), HomeTakeable {
    override val typeName = "Диск"
    override val iconId = R.drawable.diskicon

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
