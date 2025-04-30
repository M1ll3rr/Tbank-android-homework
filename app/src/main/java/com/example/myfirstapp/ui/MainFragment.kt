package com.example.myfirstapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.data.SortType
import com.example.myfirstapp.databinding.FragmentMainBinding
import com.example.myfirstapp.recycler.adapters.LibraryAdapter
import com.example.myfirstapp.recycler.itemtouchhelper.RemoveSwipeCallback
import com.example.myfirstapp.ui.ItemFragment.Companion.EXTRA_NEW_ITEM_POS
import com.example.myfirstapp.viewmodels.LibraryRepository
import com.example.myfirstapp.viewmodels.LibraryRepository.Companion.ERROR_DATABASE_REMOVE
import com.example.myfirstapp.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch


class MainFragment : Fragment(), MenuProvider {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private lateinit var libraryAdapter: LibraryAdapter
    private val viewModel by lazy {
        val repository = (requireActivity() as MainActivity).getRepository()
        ViewModelProvider(this, ViewModelFactory(repository))[MainViewModel::class.java]
    }

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
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect {
                        if (it) startShimmer() else stopShimmer()
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
                        if (it) showLoading() else hideLoading()
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.add_item -> {
                val action = MainFragmentDirections.actionMainFragmentToItemFragment(
                    itemName = null,
                    parameter1 = null,
                    parameter2 = null
                )
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

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initRecyclerView() {
        libraryAdapter = LibraryAdapter { item, position ->
            val action = ItemFragment.createAction(item, position)
            findNavController().navigate(action)
        }
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


    private fun startShimmer() {
        with(binding) {
            rcView.visibility = View.GONE
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
        }
    }

    private fun stopShimmer() {
        with(binding) {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            rcView.visibility = View.VISIBLE
        }
    }

    private fun showLoading() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        binding.rcView.stopScroll()
        binding.rcView.isNestedScrollingEnabled = false
    }

    private fun hideLoading() {
        binding.paginationProgressBar.visibility = View.GONE
        binding.rcView.isNestedScrollingEnabled = true
    }

    private fun errorHandler(error: String?) {
        if (error != null) {
            val errorMessage = LibraryRepository.errorMessages[error] ?: R.string.error_unknown
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(requireContext().getString(R.string.reload)) {
                    if (error == ERROR_DATABASE_REMOVE) {
                        libraryAdapter.notifyDataSetChanged()
                    }
                    else {
                        libraryAdapter.submitList(null)
                        viewModel.loadDatabase()
                    }
                }
                .show()
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


