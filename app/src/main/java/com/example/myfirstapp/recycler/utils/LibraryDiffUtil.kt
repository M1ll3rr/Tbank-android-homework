package com.example.myfirstapp.recycler.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.myfirstapp.library.LibraryItem

class LibraryDiffUtil(
    private val oldList: List<LibraryItem>,
    private val newList: List<LibraryItem>
    ): DiffUtil.Callback() {


    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem.access != newItem.access -> LibraryAccessChange(newItem.access)
            oldItem.id != newItem.id -> LibraryIdChange(newItem.id)
            oldItem.name != newItem.name -> LibraryNameChange(newItem.name)
            oldItem.iconId != newItem.iconId -> LibraryIconChange(newItem.iconId)
            else -> null
        }
    }

    data class LibraryAccessChange(val access: Boolean)
    data class LibraryIdChange(val id: Int)
    data class LibraryNameChange(val name: String)
    data class LibraryIconChange(val icon: Int)
}