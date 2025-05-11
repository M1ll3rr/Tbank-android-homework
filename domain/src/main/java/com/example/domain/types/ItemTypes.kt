package com.example.domain.types

enum class ItemTypes {
    BOOK,
    NEWSPAPER,
    DISK;


    companion object {
        fun getAllTypeNames() = entries
    }
}