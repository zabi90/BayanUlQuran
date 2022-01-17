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
import com.example.android.models.Surah

class SurahListAdapter (context: Context) : BaseRecyclerAdapter<Surah, SurahListAdapter.SurahListHolder>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahListHolder {

        return SurahListHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_media, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SurahListHolder, position: Int) {
       val surah: Surah = items[position]
        holder.binding.titleTextView.text = surah.title
        holder.binding.root.setOnClickListener {
            listener?.onItemSelected(surah,position,it)
        }
    }

    fun searchSurah(query: String){
        if(query.isEmpty()){
            items.addAll(tempList)
        }else{
            items.clear()
            tempList.forEachIndexed { index, surah ->

                if (surah.title.contains(query)){
                    items.add(surah)
                }
            }
            notifyDataSetChanged()
        }
    }
    class SurahListHolder(view:View) : RecyclerView.ViewHolder(view){
        val binding = ItemMediaBinding.bind(view)
    }
}