package com.example.myfirstapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.data.DiskTypes
import com.example.myfirstapp.data.ItemTypes
import com.example.myfirstapp.data.Months
import com.example.myfirstapp.databinding.ActivityItemBinding
import com.example.myfirstapp.library.Book
import com.example.myfirstapp.library.Disk
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.library.Newspaper
import com.example.myfirstapp.viewmodels.ItemActivityViewModel
import com.example.myfirstapp.viewmodels.ViewModelFactory
import android.R as AR

class ItemActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityItemBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory())[ItemActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initButtons()
        setupViewSwitchers()
    }


    private fun initButtons() {
        val backButton = binding.backButton
        val actionButton = binding.actionButton
        val access = intent.getBooleanExtra(EXTRA_ACCESS, true)

        backButton.setOnClickListener { finish() }

        if (intent.action == ACTION_ADD) actionButton.setText(R.string.add)
        else {
            if (access) actionButton.setText(R.string.take)
            else actionButton.setText(R.string.returns)
        }

        actionButton.setOnClickListener {
            if (intent.action == ACTION_ADD) addNewItem()
            else itemAction(access)
        }
    }

    private fun setupViewSwitchers() {
        if (intent.action == ACTION_ADD) {
            binding.typeNameSwitcher.displayedChild = 1
            binding.idSwitcher.displayedChild = 1
            binding.accessSwitcher.displayedChild = 1
            binding.nameSwitcher.displayedChild = 1
            binding.param2Switcher.displayedChild = 1
            setupSpinners()
        } else {
            binding.typeNameSwitcher.displayedChild = 0
            binding.idSwitcher.displayedChild = 0
            binding.accessSwitcher.displayedChild = 0
            binding.nameSwitcher.displayedChild = 0
            binding.param2Switcher.displayedChild = 0
            setParam1View(binding.param1TextView)
            fillViews()
        }
    }

    private fun setupSpinners() {
        val typeNames = ItemTypes.getAllTypeNames(this)
        val typeNameAdapter = ArrayAdapter(this, AR.layout.simple_spinner_item, typeNames)
        typeNameAdapter.setDropDownViewResource(AR.layout.simple_spinner_dropdown_item)
        binding.typeNameSpinner.adapter = typeNameAdapter

        val accessAdapter = ArrayAdapter(this, AR.layout.simple_spinner_item, listOf(getString(R.string.access_true), getString(
            R.string.access_false
        )))
        accessAdapter.setDropDownViewResource(AR.layout.simple_spinner_dropdown_item)
        binding.accessSpinner.adapter = accessAdapter

        val diskTypes = DiskTypes.getAllDiskTypes()
        val diskTypeAdapter = ArrayAdapter(this, AR.layout.simple_spinner_item, diskTypes)
        diskTypeAdapter.setDropDownViewResource(AR.layout.simple_spinner_dropdown_item)

        val months = Months.getAllMonths(this)
        val monthsAdapter = ArrayAdapter(this, AR.layout.simple_spinner_item, months)
        monthsAdapter.setDropDownViewResource(AR.layout.simple_spinner_dropdown_item)

        binding.typeNameSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val newItemType = ItemTypes.entries[position]
                    with(binding) {
                        itemIcon.setImageResource(newItemType.iconId)
                        when (newItemType) {
                            ItemTypes.BOOK -> setupBookFields()
                            ItemTypes.NEWSPAPER -> {
                                setupNewspaperFields()
                                param1Spinner.adapter = monthsAdapter
                            }
                            ItemTypes.DISK -> {
                                setupDiskFields()
                                param1Spinner.adapter = diskTypeAdapter
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) { }
            }
    }

    private fun setParam1View(
        view: View
    ) {
        with(binding) {
            when(view) {
                param1TextView -> {
                    param1TextView.visibility = View.VISIBLE
                    param1EditText.visibility = View.GONE
                    param1Spinner.visibility = View.GONE
                }
                param1EditText -> {
                    param1TextView.visibility = View.GONE
                    param1EditText.visibility = View.VISIBLE
                    param1Spinner.visibility = View.GONE
                }
                param1Spinner -> {
                    param1TextView.visibility = View.GONE
                    param1EditText.visibility = View.GONE
                    param1Spinner.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun setupBookFields() {
        with(binding) {
            setParam1View(param1EditText)
            parameter1Title.setText(R.string.author)
            parameter2Title.setText(R.string.numOfPage)
            parameter2Title.visibility = View.VISIBLE
            param2EditText.visibility = View.VISIBLE
        }
    }

    private fun setupNewspaperFields() {
        with(binding) {
            setParam1View(param1Spinner)
            parameter1Title.setText(R.string.monthOfPub)
            parameter2Title.setText(R.string.numOfPub)
            parameter2Title.visibility = View.VISIBLE
            param2EditText.visibility = View.VISIBLE
        }
    }

    private fun setupDiskFields() {
        with(binding) {
            setParam1View(param1Spinner)
            parameter1Title.setText(R.string.diskType)
            parameter2Title.visibility = View.INVISIBLE
            param2EditText.visibility = View.INVISIBLE
        }
    }

    private fun fillViews() {
        val itemTypeOrdinal = intent.getIntExtra(EXTRA_ITEM_TYPE, 0)
        val itemType = ItemTypes.entries[itemTypeOrdinal]
        fillUniversalData(itemType)
        when (itemType) {
            ItemTypes.BOOK -> fillBookData()
            ItemTypes.NEWSPAPER -> fillNewspaperData()
            ItemTypes.DISK -> fillDiscData()
        }
    }

    private fun fillUniversalData(itemType: ItemTypes) {
        with(binding) {
            itemIcon.setImageResource(itemType.iconId)
            typeNameTextView.text = itemType.getTypeName(this@ItemActivity)
            idTextView.text = intent.getIntExtra(EXTRA_ID, 0).toString()
            accessTextView.text = if (intent.getBooleanExtra(EXTRA_ACCESS, true))
                getString(R.string.access_true) else getString(R.string.access_false)
            nameTextView.text = intent.getStringExtra(EXTRA_NAME)
        }
    }


    private fun fillBookData() {
        with(binding) {
            parameter1Title.setText(R.string.author)
            param1TextView.text = intent.getStringExtra(EXTRA_AUTHOR)
            parameter2Title.setText(R.string.numOfPage)
            param2TextView.text = intent.getIntExtra(EXTRA_NUM_OF_PAGE, 0).toString()
        }
    }

    private fun fillNewspaperData() {
        with(binding) {
            parameter1Title.setText(R.string.numOfPub)
            param1TextView.text = intent.getIntExtra(EXTRA_NUM_OF_PUB, 0).toString()
            parameter2Title.setText(R.string.monthOfPub)
            val monthOrdinal = intent.getIntExtra(EXTRA_MONTH, 0)
            val month = Months.entries[monthOrdinal]
            param2TextView.text = month.getLocalName(this@ItemActivity)
        }
    }

    private fun fillDiscData() {
        with(binding) {
            parameter1Title.setText(R.string.diskType)
            param1TextView.text = intent.getStringExtra(EXTRA_DISK_TYPE)
            parameter2Title.visibility = View.INVISIBLE
            param2TextView.visibility = View.INVISIBLE
        }
    }

    private fun checkTypeFill(itemType: ItemTypes): Boolean {
        when (itemType) {
            ItemTypes.BOOK -> {
                val author = binding.param1EditText.text.toString()
                val numOfPage = binding.param2EditText.text.toString().toIntOrNull()
                if (author.isEmpty() || numOfPage == null) {
                    Toast.makeText(this, R.string.fill_error, Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            ItemTypes.NEWSPAPER -> {
                val month = Months.entries[binding.param1Spinner.selectedItemPosition]
                val numOfPub = binding.param2EditText.text.toString().toIntOrNull()
                if (numOfPub == null) {
                    Toast.makeText(this, R.string.fill_error, Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            ItemTypes.DISK -> {}
        }
        return false
    }

    private fun addNewItem() {
        val itemType = ItemTypes.entries[binding.typeNameSpinner.selectedItemPosition]
        val id = binding.idEditText.text.toString().toIntOrNull()
        val name = binding.nameEditText.text.toString()
        val access = binding.accessSpinner.selectedItemPosition == 0

        if (id == null || name.isEmpty()) {
            Toast.makeText(this, R.string.fill_error, Toast.LENGTH_SHORT).show()
            return
        }
        if (checkTypeFill(itemType)) return

        if (viewModel.isIdExists(id)) {
            Toast.makeText(this, R.string.id_error, Toast.LENGTH_SHORT).show()
            return
        }

        val resultIntent = Intent().apply {
            action = ACTION_ADD
            putExtra(EXTRA_ITEM_TYPE, itemType.ordinal)
            putExtra(EXTRA_ID, id)
            putExtra(EXTRA_ACCESS, access)
            putExtra(EXTRA_NAME, name)

            when (itemType) {
                ItemTypes.BOOK -> {
                    putExtra(EXTRA_AUTHOR, binding.param1EditText.text.toString())
                    putExtra(EXTRA_NUM_OF_PAGE, binding.param2EditText.text.toString().toInt())
                }
                ItemTypes.NEWSPAPER -> {
                    putExtra(EXTRA_NUM_OF_PUB, binding.param2EditText.text.toString().toInt())
                    putExtra(EXTRA_MONTH, binding.param1Spinner.selectedItemPosition)
                }
                ItemTypes.DISK -> {
                    putExtra(EXTRA_DISK_TYPE, DiskTypes.entries[binding.param1Spinner.selectedItemPosition].name)
                }
            }
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun itemAction(access: Boolean) {
        val newAccess = !access
        val position = intent.getIntExtra(EXTRA_POSITION, -1)
        viewModel.updateItemAccess(position, newAccess)
        setResult(RESULT_OK, Intent().putExtra(EXTRA_POSITION, position))
        finish()
    }

    companion object {
        const val ACTION_VIEW = "com.example.myfirstapp.ACTION_VIEW"
        const val ACTION_ADD = "com.example.myfirstapp.ACTION_ADD"

        const val EXTRA_ITEM_TYPE = "itemTypeOrdinal"
        const val EXTRA_POSITION = "position"
        const val EXTRA_ID = "id"
        const val EXTRA_ACCESS = "access"
        const val EXTRA_NAME = "name"
        const val EXTRA_AUTHOR = "author"
        const val EXTRA_NUM_OF_PAGE = "numOfPage"
        const val EXTRA_NUM_OF_PUB = "numOfPub"
        const val EXTRA_MONTH = "monthOrdinal"
        const val EXTRA_DISK_TYPE = "diskType"

        fun createIntent(context: Context, item: LibraryItem? = null): Intent {
            return Intent(context, ItemActivity::class.java).apply {
                if (item == null)
                    action = ACTION_ADD
                else
                    action = ACTION_VIEW

                if (item != null) {
                    putExtra(EXTRA_ITEM_TYPE, item.itemType.ordinal)
                    putExtra(EXTRA_ID, item.id)
                    putExtra(EXTRA_ACCESS, item.access)
                    putExtra(EXTRA_NAME, item.name)
                    when (item) {
                        is Book -> {
                            putExtra(EXTRA_AUTHOR, item.author)
                            putExtra(EXTRA_NUM_OF_PAGE, item.numOfPage)
                        }
                        is Newspaper -> {
                            putExtra(EXTRA_NUM_OF_PUB, item.numOfPub)
                            putExtra(EXTRA_MONTH, item.monthOfPub.ordinal)
                        }
                        is Disk -> putExtra(EXTRA_DISK_TYPE, item.diskType.name)
                    }
                }
            }
        }
    }
}