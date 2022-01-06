package com.example.android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.R
import com.example.android.adapters.FavouriteListAdapter
import com.example.android.base.BaseFragment
import com.example.android.base.BaseViewModel
import com.example.android.base.OnItemSelectListener
import com.example.android.databinding.FragmentFavouriteBinding
import com.example.android.databinding.FragmentHomeBinding
import com.example.android.extensions.toast
import com.example.android.models.AudioItem
import com.example.android.models.Surah
import com.example.android.viewmodels.FavouriteViewModel
import com.example.android.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class FavouriteFragment : BaseFragment() {

    private lateinit var adapter: FavouriteListAdapter

    private val viewModel: FavouriteViewModel by viewModels()

    private var _binding: FragmentFavouriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun setListeners() {
        adapter.addListener(object : OnItemSelectListener<AudioItem> {
            override fun onItemSelected(item: AudioItem, position: Int, view: View) {
                val surah = Surah(1,item.title, listOf(item))
                val action =
                    FavouriteFragmentDirections.actionFavouriteFragmentToMediaPlayerFragment(surah)
                findNavController().navigate(action)
            }

        })
    }

    override fun setViewModel(): BaseViewModel? {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val view = binding.root

//        val navController = findNavController()
//
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//
//        binding.toolbar2
//            .setupWithNavController(navController, appBarConfiguration)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            adapter = FavouriteListAdapter(it)
            binding.recyclerView.layoutManager = LinearLayoutManager(it)
            binding.recyclerView.adapter = adapter
        }

        viewModel.audioItems.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                binding.emptyTextView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.VISIBLE
            }
            adapter.setItems(it)
        })

        viewModel.loadFavouriteSurahList()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouriteFragment().apply {
            }
    }
}