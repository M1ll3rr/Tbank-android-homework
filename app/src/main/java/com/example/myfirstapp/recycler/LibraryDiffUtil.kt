package com.example.myfirstapp.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entities.LibraryItem

class LibraryDiffUtil: DiffUtil.ItemCallback<LibraryItem>() {
    override fun areItemsTheSame(oldItem: LibraryItem, newItem: LibraryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LibraryItem, newItem: LibraryItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: LibraryItem, newItem: LibraryItem): Any? {
        return when {
            oldItem.access != newItem.access -> LibraryAccessChange(newItem.access)
            oldItem.id != newItem.id -> LibraryIdChange(newItem.id)
            oldItem.name != newItem.name -> LibraryNameChange(newItem.name)
            else -> null
        }
    }

    data class LibraryAccessChange(val access: Boolean)
    data class LibraryIdChange(val id: Int)
    data class LibraryNameChange(val name: String)
}