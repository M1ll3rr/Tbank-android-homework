package com.example.myfirstapp.data

import android.content.Context
import com.example.myfirstapp.R

enum class ItemTypes(private val typeNameId: Int, val iconId: Int) {
    BOOK(R.string.book, R.drawable.bookicon),
    NEWSPAPER(R.string.newspaper, R.drawable.newspapericon),
    DISK(R.string.disk, R.drawable.diskicon);

    fun getTypeName(context: Context) = context.getString(typeNameId)

    companion object {
        fun getAllTypeNames(context: Context) = entries.map { it.getTypeName(context) }
    }
}