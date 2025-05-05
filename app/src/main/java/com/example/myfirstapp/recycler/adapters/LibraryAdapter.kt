package com.example.myfirstapp.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.myfirstapp.databinding.ListItemBinding
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.recycler.utils.LibraryDiffUtil
import com.example.myfirstapp.recycler.vh.LibraryViewHolder

class LibraryAdapter(private val onClick: (LibraryItem, Int) -> Unit, private val onLongClick: (LibraryItem) -> Unit):
    ListAdapter<LibraryItem, LibraryViewHolder>(LibraryDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return LibraryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick(item, position)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun onBindViewHolder(
        holder: LibraryViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
        else {
            payloads.forEach {
                when (it) {
                    is LibraryDiffUtil.LibraryAccessChange -> holder.bindAccess(it.access)
                    is LibraryDiffUtil.LibraryIdChange -> holder.bindId(it.id)
                    is LibraryDiffUtil.LibraryNameChange -> holder.bindName(it.name)
                    is LibraryDiffUtil.LibraryIconChange -> holder.bindIcon(it.icon)
                }
            }
        }
    }
}