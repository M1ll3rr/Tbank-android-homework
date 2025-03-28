package com.example.myfirstapp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.LibraryItemBinding
import com.example.myfirstapp.library.LibraryObject
import com.example.myfirstapp.recycler.vh.LibraryViewHolder

class LibraryAdapter: RecyclerView.Adapter<LibraryViewHolder>() {
    private val data = mutableListOf<LibraryObject>()

    fun setNewData(newData: List<LibraryObject>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = LibraryItemBinding.inflate(LayoutInflater.from(parent.context))
        return LibraryViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(data[position])
    }

}