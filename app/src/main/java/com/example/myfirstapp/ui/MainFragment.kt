package com.example.myfirstapp.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragmentMainBinding
import com.example.myfirstapp.recycler.adapters.LibraryAdapter
import com.example.myfirstapp.recycler.itemtouchhelper.RemoveSwipeCallback
import com.example.myfirstapp.viewmodels.LibraryRepository
import com.example.myfirstapp.viewmodels.LibraryRepository.Companion.ERROR_DATABASE_LOAD
import com.example.myfirstapp.viewmodels.LibraryRepository.Companion.ERROR_UNKNOWN
import com.example.myfirstapp.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch


class MainFragment : Fragment(), MenuProvider {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private lateinit var libraryAdapter: LibraryAdapter
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }
    private var targetTime = 0L


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initViewModel()
        initRecyclerView()
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

    private fun updateLoadingHandler(loading: Boolean) {
        if (loading) {
            targetTime = System.currentTimeMillis() + 1000L
            startShimmer()
        } else {
            if (System.currentTimeMillis() >= targetTime) {
                stopShimmer()
            } else {
                val remainingTime = targetTime - System.currentTimeMillis()
                Handler(Looper.getMainLooper()).postDelayed(
                    { stopShimmer() },
                    remainingTime
                )
            }
        }

    }

    private fun updateErrorHandler(errorCode: String?) {
        if (errorCode != null && errorCode != "Job was cancelled") {
            val errorMessage = LibraryRepository.errorMessages[errorCode] ?: R.string.error_unknown
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(requireContext().getString(R.string.reload)) {
                    viewModel.clearError()
                    if (errorCode == ERROR_UNKNOWN || errorCode == ERROR_DATABASE_LOAD) {
                        viewModel.loadItems()
                    }
                    else {
                        viewModel.repeatLastAction()
                        if (viewModel.lastActionType == LibraryRepository.ActionType.UPDATE) {
                            libraryAdapter.notifyItemChanged(viewModel.lastUpdatedPosition ?: 0)
                        }
                    }
                }
                .show()
        }
    }

    private fun setNewScrollListener() {
        if (viewModel.lastActionType == LibraryRepository.ActionType.ADD && viewModel.error.value == null) {
            viewModel.setNewItemScrollPosition()
            with(binding.rcView) {
                post {
                    smoothScrollToPosition(viewModel.getScrollPosition)
                }
            }
        }
    }

    private fun initViewModel() {
        if (!viewModel.getIsDataLoaded) viewModel.loadItems()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    libraryAdapter.submitList(it)
                    setNewScrollListener()
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect {
                    if (!viewModel.getIsDataLoaded) updateLoadingHandler(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect {
                    updateErrorHandler(it)
                }
            }
        }
    }
}


