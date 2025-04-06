package com.example.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myfirstapp.data.LibraryData
import com.example.myfirstapp.library.LibraryItem

class MainViewModel: ViewModel() {
    private val _items = MutableLiveData<List<LibraryItem>>(LibraryData.items)
    val items: LiveData<List<LibraryItem>> = _items

    fun addItem(item: LibraryItem) {
        val oldList = _items.value?.toMutableList()
        oldList?.add(item)
        _items.value = oldList?.sortedBy { it.id }
    }

    fun removeItem(position: Int) {
        val oldList = _items.value?.toMutableList()
        oldList?.removeAt(position)
        _items.value = oldList?.sortedBy { it.id }
    }

    fun updateItemAccess(position: Int, newAccess: Boolean) {
        val oldList = _items.value?.toMutableList()
        oldList!![position].access = newAccess
        _items.value = oldList
    }

    fun isIdExists(id: Int): Boolean {
        return _items.value?.any() { it.id == id } ?: false
    }
}