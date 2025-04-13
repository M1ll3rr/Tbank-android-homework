package com.example.myfirstapp.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.ListItemBinding
import com.example.myfirstapp.fragment.ItemFragment
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.recycler.utils.LibraryDiffUtil
import com.example.myfirstapp.recycler.vh.LibraryViewHolder

class LibraryAdapter(private val navController: NavController): RecyclerView.Adapter<LibraryViewHolder>() {
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
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return LibraryViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = data[position]
                    val action = ItemFragment.createAction(item, position)
                    navController.navigate(action)
                }
            }
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
}