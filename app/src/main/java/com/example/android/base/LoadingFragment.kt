package com.example.android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.android.R
import com.example.android.databinding.LoadingFragmentBinding


class LoadingFragment : DialogFragment() {


    private var _binding: LoadingFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = LoadingFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setTitle(title:String){
        binding.statusTextView.text = title
    }
}