package com.example.myfirstapp.data

enum class DiskTypes {
    CD,
    DVD;


    companion object {
        fun getAllDiskTypes() = DiskTypes.entries.map { it.name }
    }
}