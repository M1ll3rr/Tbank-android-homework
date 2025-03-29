package com.example.myfirstapp.recycler.vh

import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.LibraryItemBinding
import com.example.myfirstapp.library.LibraryItem

class LibraryViewHolder(private val binding: LibraryItemBinding):
    RecyclerView.ViewHolder(binding.root) {
    fun bind(libraryItem: LibraryItem) = with(binding) {
        bindId(libraryItem.id)
        bindName(libraryItem.name)
        bindIcon(libraryItem.iconId)
        bindAccess(libraryItem.access)
    }

    fun bindId(newId: Int) = with(binding) {
        itemId.text = newId.toString()
    }

    fun bindName(newName: String) = with(binding) {
        itemName.text = newName
    }

    fun bindIcon(newIcon: Int) = with(binding) {
        itemIcon.setImageResource(newIcon)
    }

    fun bindAccess(access: Boolean) = with(binding) {
        val density = root.resources.displayMetrics.density
        if (access) {
            itemId.alpha = 1f
            itemName.alpha = 1f
            root.elevation = 10f * density
        }
        else {
            itemId.alpha = 0.3f
            itemName.alpha = 0.3f
            root.elevation = 1f * density
        }
    }

}