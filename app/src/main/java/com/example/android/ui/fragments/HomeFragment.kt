package com.example.android.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android.R
import com.example.android.adapters.SurahListAdapter
import com.example.android.base.BaseFragment
import com.example.android.base.BaseViewModel
import com.example.android.base.OnItemSelectListener
import com.example.android.databinding.FragmentHomeBinding
import com.example.android.extensions.toast
import com.example.android.models.AudioItem
import com.example.android.models.Surah
import com.example.android.viewmodels.HomeViewModel
import com.example.android.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var adapter: SurahListAdapter? = null

    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)b
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            adapter = SurahListAdapter(it)
            binding.surahList.layoutManager = LinearLayoutManager(it)
            binding.surahList.adapter = adapter
        }

        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        })

        viewModel.audioItems.observe(viewLifecycleOwner, {
            adapter?.setItems(it)
        })

        val navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view.findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSurahList(shouldRefresh = false)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //region Base class and interface override methods
    override fun setListeners() {
        adapter?.addListener(object : OnItemSelectListener<Surah> {
            override fun onItemSelected(item: Surah, position: Int, view: View) {
                val action = HomeFragmentDirections.actionHomeFragmentToMediaPlayerFragment(item)
                findNavController().navigate(action)
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadSurahList(shouldRefresh = true)
        }
    }

    override fun setViewModel(): BaseViewModel {
        return viewModel
    }

    //endregion
    companion object {

        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
            }
    }
}