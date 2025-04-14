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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragmentMainBinding
import com.example.myfirstapp.ui.ItemFragment.Companion.EXTRA_NEW_ITEM_POS
import com.example.myfirstapp.ui.ItemFragment.Companion.NEW_ITEM_REQUEST
import com.example.myfirstapp.recycler.adapters.LibraryAdapter
import com.example.myfirstapp.recycler.itemtouchhelper.RemoveSwipeCallback
import com.example.myfirstapp.viewmodels.ViewModelFactory
import dev.androidbroadcast.vbpd.viewBinding


class MainFragment : Fragment(), MenuProvider {
    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private val libraryAdapter by lazy {
        LibraryAdapter(findNavController())
    }
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }
    private var scrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(NEW_ITEM_REQUEST) { _, bundle ->
            scrollPosition = bundle.getInt(EXTRA_NEW_ITEM_POS)
            with(binding.rcView) {
                post { smoothScrollToPosition(scrollPosition) }
            }
        }
        initToolbar()
        initViewModel()
        initRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        val layoutManager = binding.rcView.layoutManager as LinearLayoutManager
        scrollPosition = layoutManager.findFirstVisibleItemPosition()
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
        with(binding.rcView) {
            layoutManager = LinearLayoutManager(context)
            adapter = libraryAdapter
            scrollToPosition(scrollPosition)
            ItemTouchHelper(RemoveSwipeCallback {
                viewModel.removeItem(it)
            }).attachToRecyclerView(this)
        }
    }

    private fun initViewModel() {
        viewModel.items.observe(viewLifecycleOwner) {
            libraryAdapter.setNewData(it)
        }
    }


}