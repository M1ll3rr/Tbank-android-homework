package com.example.myfirstapp.recycler.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.LibraryItemBinding
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.recycler.utils.LibraryDiffUtil
import com.example.myfirstapp.recycler.vh.LibraryViewHolder

class LibraryAdapter: RecyclerView.Adapter<LibraryViewHolder>() {
    private val data = mutableListOf<LibraryItem>()

    override fun getItemCount() = data.size

    fun setNewData(newData: List<LibraryItem>) {
        val diffUtil = LibraryDiffUtil(data, newData)
        DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LibraryItemBinding.inflate(inflater, parent, false)
        return LibraryViewHolder(binding).apply {
            binding.root.setOnClickListener {
                handleLibraryClick(parent.context, adapterPosition)
            }
        }
    }

    private fun handleLibraryClick(context: Context, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val item = data[position]
            item.access = !item.access
            notifyItemChanged(position, LibraryDiffUtil.LibraryAccessChange(item.access))
            Toast.makeText(context, "Элемент с id ${item.id}", LENGTH_SHORT).show()
        }
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(data[position])
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

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

}