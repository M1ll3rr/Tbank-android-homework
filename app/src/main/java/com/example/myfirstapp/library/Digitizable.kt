package com.example.myfirstapp.library

import com.example.myfirstapp.data.ItemTypes

interface Digitizable {
    val id: Int
    val name: String
    val itemType: ItemTypes
}