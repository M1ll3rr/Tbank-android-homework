package com.example.myfirstapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.domain.entities.Book
import com.example.domain.entities.Disk
import com.example.domain.entities.LibraryItem
import com.example.domain.entities.Newspaper
import com.example.domain.types.DiskTypes
import com.example.domain.types.ItemTypes
import com.example.domain.types.Months
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragmentItemBinding
import com.example.myfirstapp.mappers.ResourceMapper
import com.example.myfirstapp.viewmodels.ItemViewModel
import com.example.myfirstapp.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ItemFragment : Fragment() {
    private val binding: FragmentItemBinding by viewBinding(FragmentItemBinding::bind)
    private val args: ItemFragmentArgs by navArgs()
    private val resourceMapper by lazy { ResourceMapper(requireContext()) }
    private val viewModel by lazy {
        val libraryUseCases = (requireActivity() as MainActivity).getLibraryUseCases
        ViewModelProvider(this, ViewModelFactory(libraryUseCases))[ItemViewModel::class.java]
    }
    private val addMode by lazy {
        args.itemId == -1
    }

    private var itemJob: Job? = null

    override fun onDestroy() {
        itemJob?.cancel()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        setupViewSwitchers()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.error.collect { errorHandler(it) }
                }
                launch {
                    viewModel.isLoading.collect {
                        if (it) binding.progressBar.visibility = View.VISIBLE
                        else binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun initButtons() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        val actionButton = binding.actionButton
        val access = args.itemAccess

        if (addMode) actionButton.setText(R.string.add)
        else {
            if (access) actionButton.setText(R.string.take)
            else actionButton.setText(R.string.returns)
        }

        actionButton.setOnClickListener {
            if (addMode) addNewItem()
            else itemAction()
        }
    }

    private fun setupViewSwitchers() {
        if (addMode) {
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
        val typeNames = ItemTypes.getAllTypeNames().map {
            resourceMapper.getItemTypeName(it)
        }
        val typeNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeNames)
        typeNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeNameSpinner.adapter = typeNameAdapter

        val accessAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf(getString(R.string.access_true), getString(
            R.string.access_false
        )))
        accessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.accessSpinner.adapter = accessAdapter

        val diskTypes = DiskTypes.getAllDiskTypes()
        val diskTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, diskTypes)
        diskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val months = Months.getAllMonths().map {
            resourceMapper.getMonthName(it)
        }
        val monthsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

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
                        itemIcon.setImageResource(resourceMapper.getItemIcon(newItemType))
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
        val itemTypeOrdinal = args.itemTypeOrdinal
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
            itemIcon.setImageResource(resourceMapper.getItemIcon(itemType))
            typeNameTextView.text = resourceMapper.getItemTypeName(itemType)
            idTextView.text = args.itemId.toString()
            accessTextView.text = if (args.itemAccess) getString(R.string.access_true) else getString(R.string.access_false)
            nameTextView.text = args.itemName
        }
    }

    private fun fillBookData() {
        with(binding) {
            parameter1Title.setText(R.string.author)
            param1TextView.text = args.parameter1
            parameter2Title.setText(R.string.numOfPage)
            param2TextView.text = args.parameter2
        }
    }

    private fun fillNewspaperData() {
        with(binding) {
            parameter1Title.setText(R.string.numOfPub)
            param1TextView.text = args.parameter1
            parameter2Title.setText(R.string.monthOfPub)
            val monthOrdinal = args.parameter2?.toInt()
            val month = Months.entries[monthOrdinal!!]
            param2TextView.text = resourceMapper.getMonthName(month)
        }
    }

    private fun fillDiscData() {
        with(binding) {
            parameter1Title.setText(R.string.diskType)
            param1TextView.text = args.parameter1
            parameter2Title.visibility = View.INVISIBLE
            param2TextView.visibility = View.INVISIBLE
        }
    }


    private fun checkTypeFill(itemType: ItemTypes): Boolean {
        when (itemType) {
            ItemTypes.BOOK -> {
                val author = binding.param1EditText.text.toString().trim()
                val numOfPage = binding.param2EditText.text.toString().toIntOrNull()
                if (author.isEmpty() || numOfPage == null) {
                    Toast.makeText(requireContext(), R.string.error_fill, Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            ItemTypes.NEWSPAPER -> {
                val month = Months.entries[binding.param1Spinner.selectedItemPosition]
                val numOfPub = binding.param2EditText.text.toString().toIntOrNull()
                if (numOfPub == null) {
                    Toast.makeText(requireContext(), R.string.error_fill, Toast.LENGTH_SHORT).show()
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
        val name = binding.nameEditText.text.toString().trim()
        val access = binding.accessSpinner.selectedItemPosition == 0

        if (id == null || name.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_fill, Toast.LENGTH_SHORT).show()
            return
        }
        if (checkTypeFill(itemType)) return

        itemJob = lifecycleScope.launch {
            if (viewModel.isIdExists(id)) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), R.string.error_id, Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val newItem = when (itemType) {
                ItemTypes.BOOK -> createBook(id, name, access)
                ItemTypes.NEWSPAPER -> createNewspaper(id, name, access)
                ItemTypes.DISK -> createDisk(id, name, access)
            }

            val newItemPos = viewModel.addItem(newItem)
            if (newItemPos != -1) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(EXTRA_NEW_ITEM_POS, newItemPos)
                findNavController().navigateUp()
            }
        }
    }

    private fun createBook(id: Int, name: String, access: Boolean) : Book {
        val author = binding.param1EditText.text.toString().trim()
        val numOfPage = binding.param2EditText.text.toString().toInt()
        return Book(id, name, author, numOfPage, access)
    }

    private fun createNewspaper(id: Int, name: String, access: Boolean) : Newspaper {
        val numOfPub = binding.param2EditText.text.toString().toInt()
        val monthOrdinal = binding.param1Spinner.selectedItemPosition
        val month = Months.entries[monthOrdinal]
        return Newspaper(id, name, numOfPub, month, access)
    }

    private fun createDisk(id: Int, name: String, access: Boolean) : Disk {
        val diskTypeName = DiskTypes.entries[binding.param1Spinner.selectedItemPosition].name
        val diskType = DiskTypes.valueOf(diskTypeName)
        return Disk(id, name, diskType, access)
    }

    private fun itemAction() {
        itemJob = lifecycleScope.launch {
            val result = viewModel.updateItemAccess(args.position)
            if (result) findNavController().navigateUp()
        }
    }



    private fun errorHandler(error: String?) {
        if (error != null) {
            val errorMessage = resourceMapper.getErrorMessage(error)
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.itemIcon)
                .show()
        }
    }


    companion object {
        const val EXTRA_ITEM_TYPE = "itemTypeOrdinal"
        const val EXTRA_POSITION = "position"
        const val EXTRA_ID = "id"
        const val EXTRA_ACCESS = "access"
        const val EXTRA_NAME = "name"
        const val EXTRA_PARAM1 = "parameter1"
        const val EXTRA_PARAM2 = "parameter2"
        const val EXTRA_NEW_ITEM_POS = "newItemPos"

        fun createAction(item: LibraryItem, position: Int): NavDirections {
            val params = mutableMapOf<String, Any?>(
                EXTRA_POSITION to position,
                EXTRA_ITEM_TYPE to item.itemType.ordinal,
                EXTRA_ID to item.id,
                EXTRA_NAME to item.name,
                EXTRA_ACCESS to item.access
            )
            when (item) {
                is Book -> {
                    params[EXTRA_PARAM1] = item.author
                    params[EXTRA_PARAM2] = item.numOfPage.toString()
                }

                is Disk -> {
                    params[EXTRA_PARAM1] = item.diskType.name
                    params[EXTRA_PARAM2] = null
                }

                is Newspaper -> {
                    params[EXTRA_PARAM1] = item.numOfPub.toString()
                    params[EXTRA_PARAM2] = item.monthOfPub.ordinal.toString()
                }
            }
            return MainFragmentDirections.actionMainFragmentToItemFragment(
                position = params[EXTRA_POSITION] as Int,
                itemTypeOrdinal = params[EXTRA_ITEM_TYPE] as Int,
                itemId = params[EXTRA_ID] as Int,
                itemName = params[EXTRA_NAME] as String,
                itemAccess = params[EXTRA_ACCESS] as Boolean,
                parameter1 = params[EXTRA_PARAM1] as String,
                parameter2 = params[EXTRA_PARAM2] as String?
            )
        }
    }
}