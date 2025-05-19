package com.example.myfirstapp.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entities.LibraryItem
import com.example.myfirstapp.databinding.ListItemBinding
import com.example.myfirstapp.mappers.ResourceMapper

class LibraryViewHolder(private val binding: ListItemBinding):
    RecyclerView.ViewHolder(binding.root) {
    private val resourceMapper by lazy { ResourceMapper(binding.root.context) }

    fun bind(libraryItem: LibraryItem) {
        bindId(libraryItem.id)
        bindName(libraryItem.name)
        bindIcon(resourceMapper.getItemIcon(libraryItem.itemType))
        return bindAccess(libraryItem.access)
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