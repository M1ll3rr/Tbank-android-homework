package com.example.myfirstapp.recycler.vh

import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.LibraryItemBinding
import com.example.myfirstapp.library.LibraryObject

class LibraryViewHolder(private val binding: LibraryItemBinding):
    RecyclerView.ViewHolder(binding.root) {
    fun bind(libraryObject: LibraryObject) = with(binding) {
        bindId(libraryObject.id)
        bindName(libraryObject.name)
        bindIcon(libraryObject.iconId)
        bindAccess(libraryObject.access)
    }

    private fun bindId(newId: Int) = with(binding) {
        itemId.text = newId.toString()
    }

    private fun bindName(newName: String) = with(binding) {
        itemName.text = newName
    }

    private fun bindIcon(newIcon: Int) = with(binding) {
        itemIcon.setImageResource(newIcon)
    }

    private fun bindAccess(access: Boolean) = with(binding) {
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