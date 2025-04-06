package com.example.myfirstapp.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.viewmodels.MainViewModel
import com.example.myfirstapp.R
import com.example.myfirstapp.data.DiskTypes
import com.example.myfirstapp.data.ItemTypes
import com.example.myfirstapp.data.LibraryData
import com.example.myfirstapp.data.Months
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.example.myfirstapp.library.Book
import com.example.myfirstapp.library.Disk
import com.example.myfirstapp.library.Newspaper
import com.example.myfirstapp.recycler.adapters.LibraryAdapter
import com.example.myfirstapp.recycler.itemtouchhelper.RemoveSwipeCallback

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var libraryAdapter: LibraryAdapter
    private val viewModel: MainViewModel by viewModels()

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data?.getStringExtra("mode") == "changeAccess") {
                val position = result.data?.getIntExtra("position", -1) ?: -1
                val newAccess = result.data?.getBooleanExtra("access", true) ?: true

                if (position != -1) {
                    viewModel.updateItemAccess(position, newAccess)
                    libraryAdapter.notifyItemChanged(position)
                }
            }
            else {
                val itemTypeOrdinal = result.data?.getIntExtra("itemTypeOrdinal", 0) ?: 0
                val itemType = ItemTypes.entries[itemTypeOrdinal]
                val id = result.data?.getIntExtra("id", 0)
                val access = result.data?.getBooleanExtra("access", true)
                val name = result.data?.getStringExtra("name")
                when (itemType) {
                    ItemTypes.BOOK -> createBook(result, id!!, name!!, access!!)
                    ItemTypes.NEWSPAPER -> createNewspaper(result, id!!, name!!, access!!)
                    ItemTypes.DISK -> createDisk(result, id!!, name!!, access!!)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        libraryAdapter = LibraryAdapter(startForResult)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initViewModel()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_item -> {
                addItemAction()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRecyclerView() {
        with(binding.rcView) {
            layoutManager = LinearLayoutManager(context)
            adapter = libraryAdapter
            ItemTouchHelper(RemoveSwipeCallback {
                viewModel.removeItem(it)
            }).attachToRecyclerView(this)
        }
    }

    private fun addItemAction() {
        startForResult.launch(
            ItemActivity.createIntent(this, "add")
        )
    }

    private fun initViewModel() {
        viewModel.items.observe(this) {
            libraryAdapter.setNewData(it)
        }
    }

    private fun createBook(result: ActivityResult, id: Int, name: String, access: Boolean) {
        val author = result.data?.getStringExtra("author")
        val numOfPage = result.data?.getIntExtra("numOfPage", 0)
        viewModel.addItem(Book(id, name, author!!, numOfPage!!, access))
    }

    private fun createNewspaper(result: ActivityResult, id: Int, name: String, access: Boolean) {
        val numOfPub = result.data?.getIntExtra("numOfPub", 0)
        val monthOrdinal = result.data?.getIntExtra("monthOrdinal", 0) ?:0
        val month = Months.entries[monthOrdinal]
        viewModel.addItem(Newspaper(id, name, numOfPub!!, month, access))
    }

    private fun createDisk(result: ActivityResult, id: Int, name: String, access: Boolean) {
        val diskType = result.data?.getStringExtra("diskType")
        viewModel.addItem(Disk(id, name, DiskTypes.valueOf(diskType!!), access))
    }

}