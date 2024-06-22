package com.dicoding.picodiploma.loginwithanimation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainContract
import com.dicoding.picodiploma.loginwithanimation.view.main.RVAdapter

class MyAdapter(
    private val context: Context,
    private val mainContract: MainContract,
) : PagingDataAdapter<DetailStory, MyViewHolder>(MyDiffCallback()) {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        println("item di adapter $item")
        if (item != null){
            holder.bind(item,context,mainContract)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items,parent,false)
        return MyViewHolder(view)
    }

}

class MyDiffCallback : DiffUtil.ItemCallback<DetailStory>() {
    override fun areItemsTheSame(oldItem: DetailStory, newItem: DetailStory): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: DetailStory, newItem: DetailStory): Boolean {
        return oldItem == newItem
    }

}