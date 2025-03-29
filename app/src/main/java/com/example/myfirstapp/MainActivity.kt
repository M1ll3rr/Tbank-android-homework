package com.example.myfirstapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.data.LibraryData
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.example.myfirstapp.recycler.adapters.LibraryAdapter
import com.example.myfirstapp.recycler.itemtouchhelper.RemoveSwipeCallback

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val libraryAdapter = LibraryAdapter()

    private val items = LibraryData.items


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        libraryAdapter.setNewData(items)
        with(binding.rcView) {
            layoutManager = LinearLayoutManager(context)
            adapter = libraryAdapter
            ItemTouchHelper(RemoveSwipeCallback(libraryAdapter)).attachToRecyclerView(this)
        }

    }
}