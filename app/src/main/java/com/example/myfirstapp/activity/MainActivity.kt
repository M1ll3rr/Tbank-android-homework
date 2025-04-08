package com.example.myfirstapp.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.R
import com.example.myfirstapp.data.DiskTypes
import com.example.myfirstapp.data.ItemTypes
import com.example.myfirstapp.data.Months
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.example.myfirstapp.library.Book
import com.example.myfirstapp.library.Disk
import com.example.myfirstapp.library.Newspaper
import com.example.myfirstapp.recycler.adapters.LibraryAdapter
import com.example.myfirstapp.recycler.itemtouchhelper.RemoveSwipeCallback
import com.example.myfirstapp.viewmodels.MainViewModel
import com.example.myfirstapp.viewmodels.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var libraryAdapter: LibraryAdapter
    private lateinit var viewModel: MainViewModel

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data?.hasExtra(ItemActivity.EXTRA_POSITION) == true) {
                val position = result.data?.getIntExtra(ItemActivity.EXTRA_POSITION, -1) ?: -1
                if (position != -1) libraryAdapter.notifyItemChanged(position)
            }
            else {
                val itemTypeOrdinal = result.data?.getIntExtra(ItemActivity.EXTRA_ITEM_TYPE, 0) ?: 0
                val itemType = ItemTypes.entries[itemTypeOrdinal]
                val id = result.data?.getIntExtra(ItemActivity.EXTRA_ID, 0)
                val access = result.data?.getBooleanExtra(ItemActivity.EXTRA_ACCESS, true)
                val name = result.data?.getStringExtra(ItemActivity.EXTRA_NAME)
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
            ItemActivity.createIntent(this)
        )
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
        viewModel.items.observe(this) {
            libraryAdapter.setNewData(it)
        }
    }

    private fun createBook(result: ActivityResult, id: Int, name: String, access: Boolean) {
        val author = result.data?.getStringExtra(ItemActivity.EXTRA_AUTHOR)
        val numOfPage = result.data?.getIntExtra(ItemActivity.EXTRA_NUM_OF_PAGE, 0)
        viewModel.addItem(Book(id, name, author!!, numOfPage!!, access))
    }

    private fun createNewspaper(result: ActivityResult, id: Int, name: String, access: Boolean) {
        val numOfPub = result.data?.getIntExtra(ItemActivity.EXTRA_NUM_OF_PUB, 0)
        val monthOrdinal = result.data?.getIntExtra(ItemActivity.EXTRA_MONTH, 0) ?:0
        val month = Months.entries[monthOrdinal]
        viewModel.addItem(Newspaper(id, name, numOfPub!!, month, access))
    }

    private fun createDisk(result: ActivityResult, id: Int, name: String, access: Boolean) {
        val diskType = result.data?.getStringExtra(ItemActivity.EXTRA_DISK_TYPE)
        viewModel.addItem(Disk(id, name, DiskTypes.valueOf(diskType!!), access))
    }

}