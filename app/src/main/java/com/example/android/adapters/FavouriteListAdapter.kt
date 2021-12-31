package com.example.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.base.BaseRecyclerAdapter
import com.example.android.databinding.ItemMediaBinding
import com.example.android.models.AudioItem

class FavouriteListAdapter (context: Context) : BaseRecyclerAdapter<AudioItem, FavouriteListAdapter.SurahListHolder>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahListHolder {

        return SurahListHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_media_favourite, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SurahListHolder, position: Int) {
       val audioItem: AudioItem = items[position]
        holder.binding.titleTextView.text = audioItem.title
        holder.binding.root.setOnClickListener {
            listener?.onItemSelected(audioItem,position,it)
        }
    }

    class SurahListHolder(view:View) : RecyclerView.ViewHolder(view){
        val binding = ItemMediaBinding.bind(view)
    }
}