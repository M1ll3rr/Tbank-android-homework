package com.example.myfirstapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.types.SortType
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragmentMainBinding
import com.example.myfirstapp.mappers.ResourceMapper
import com.example.myfirstapp.recycler.LibraryAdapter
import com.example.myfirstapp.recycler.RemoveSwipeCallback
import com.example.myfirstapp.ui.ItemFragment.Companion.EXTRA_NEW_ITEM_POS
import com.example.myfirstapp.viewmodels.MainViewModel
import com.example.myfirstapp.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch


class MainFragment : Fragment(), MenuProvider {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private lateinit var libraryAdapter: LibraryAdapter
    private val viewModel by lazy {
        val libraryUseCases = (requireActivity() as MainActivity).libraryUseCases
        val preferencesUseCases = (requireActivity() as MainActivity).preferencesUseCases
        ViewModelProvider(this, ViewModelFactory(libraryUseCases, preferencesUseCases))[MainViewModel::class.java]
    }
    private val resourceMapper by lazy { ResourceMapper(requireContext()) }

    private var menu: Menu? = null
    private var isReturned = false
    private var isLocal = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecyclerView()
        initButtons()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect {
                        updateShimmer(it)
                    }
                }
                launch {
                    viewModel.items.collect {
                        libraryAdapter.submitList(it)
                        setNewScrollListener()
                    }
                }
                launch {
                    viewModel.error.collect {
                        errorHandler(it)
                    }
                }
                launch {
                    viewModel.isLoadingMore.collect {
                        updateLoading(it)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val layoutManager = binding.rcView.layoutManager as LinearLayoutManager
        viewModel.setScrollPosition(layoutManager.findFirstVisibleItemPosition())
    }

    override fun onResume() {
        super.onResume()
        isReturned = false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        this.menu = menu
        menuInflater.inflate(R.menu.main_menu, menu)
        updateMenuItems(isLocal)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.add_item -> {
                val action = MainFragmentDirections.actionMainFragmentToItemFragment(
                    itemName = null,
                    parameter1 = null,
                    parameter2 = null
                )
                isReturned = true
                findNavController().navigate(action)
                true
            }
            R.id.sort_by_name -> {
                viewModel.setSortType(SortType.BY_NAME)
                true
            }
            R.id.sort_by_date -> {
                viewModel.setSortType(SortType.BY_DATE)
                true
            }
            else -> false
        }
    }

    private fun updateMenuItems(visibility: Boolean) {
        menu?.let {
            it.findItem(R.id.add_item)?.isVisible = visibility
            it.findItem(R.id.sort_menu)?.isVisible = visibility
        }
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initRecyclerView() {
        libraryAdapter = LibraryAdapter(
            onClick = { item, position ->
                val action = ItemFragment.createAction(item, position)
                isReturned = true
                findNavController().navigate(action)
            },
            onLongClick = { item ->
                if (!isLocal) {
                    lifecycleScope.launch {
                        val success = viewModel.addItem(item)
                        if (success) {
                            Toast.makeText(requireContext(), getString(R.string.item_added), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
        with(binding.rcView) {
            layoutManager = LinearLayoutManager(context)
            adapter = libraryAdapter
            scrollToPosition(viewModel.getScrollPosition)
            ItemTouchHelper(RemoveSwipeCallback {
                viewModel.removeItem(it)
            }).attachToRecyclerView(this)
            clearOnScrollListeners()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    viewModel.setScrollPosition(firstVisibleItemPosition)
                }
            })
        }
    }

    private fun initButtons() {
        with(binding) {
            buttonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (paginationProgressBar.isVisible) paginationProgressBar.visibility = View.GONE

                if (checkedId == R.id.libraryButton && isChecked) {
                    Log.d("test", "libraryButton")
                    isLocal = true
                    searchFieldsLayout.visibility = View.GONE
                    if (isReturned) return@addOnButtonCheckedListener
                    updateMenuItems(true)
                    viewModel.loadLocalItems()
                }
                else if (checkedId == R.id.googleBooksButton && isChecked) {
                    Log.d("test", "googleBooksButton")
                    isLocal = false
                    if (shimmerLayout.isVisible) shimmerLayout.visibility = View.GONE
                    rcView.visibility = View.INVISIBLE
                    searchFieldsLayout.visibility = View.VISIBLE
                    if (isReturned) return@addOnButtonCheckedListener
                    updateMenuItems(false)
                    viewModel.clearItems()
                }
            }
            titleEditText.addTextChangedListener {
                validateInput()
            }
            authorEditText.addTextChangedListener {
                validateInput()
            }
            searchButton.setOnClickListener {
                val author = binding.authorEditText.text.toString().trim()
                val title = binding.titleEditText.text.toString().trim()
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                titleEditText.clearFocus()
                authorEditText.clearFocus()
                viewModel.searchGoogleBooks(title, author)
            }
            titleEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && searchButton.isEnabled) {
                    searchButton.performClick()
                    true
                } else {
                    false
                }
            }
            authorEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && searchButton.isEnabled) {
                    searchButton.performClick()
                    true
                } else {
                    false
                }
            }
        }
    }


    private fun validateInput() {
        with(binding) {
            val isTitleValid = titleEditText.text.toString().trim().length >= 3
            val isAuthorValid = authorEditText.text.toString().trim().length >= 3
            searchButton.isEnabled = isTitleValid || isAuthorValid
        }
    }


    private fun updateShimmer(isLoading: Boolean) {
        with(binding) {
            rcView.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            shimmerLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
            shimmerLayout.showShimmer(isLoading)
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        with(binding) {
            paginationProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) rcView.stopScroll()
            rcView.isNestedScrollingEnabled = !isLoading
        }
    }

    private fun errorHandler(error: String?) {
        if (error != null) {
            if (isLocal) {
                val errorMessage = resourceMapper.getErrorMessage(error)
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(requireContext().getString(R.string.reload)) {
                        libraryAdapter.submitList(null)
                        viewModel.loadDatabase()
                    }
                    .show()
            }
            else {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun setNewScrollListener() {
        val newItemPos = findNavController().currentBackStackEntry?.savedStateHandle?.get<Int>(
            EXTRA_NEW_ITEM_POS
        ) ?: -1
        if (newItemPos != -1) {
            with(binding.rcView) {
                post {
                    smoothScrollToPosition(newItemPos)
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<Int>(EXTRA_NEW_ITEM_POS)
                }
            }
        }
    }
}


