package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory
import com.squareup.picasso.Picasso


class RVAdapter(
    private val context: Context,
    private val mainContract: MainContract,
    private val dataList: MutableList<DetailStory>): RecyclerView.Adapter<RVAdapter.MyViewHolder>(){

    class MyViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val tv_item_name = view.findViewById<TextView>(R.id.title)
        val avatar = view.findViewById<ImageView>(R.id.img_item_photo)
        val card_view = view.findViewById<CardView>(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.items,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        holder.tv_item_name.text = dataList.get(position).name
        Picasso.get().load(dataList[position].photoUrl).into(holder.avatar)
        holder.card_view.setOnClickListener{
            Toast.makeText(context, "" + dataList.get(position).id, Toast.LENGTH_SHORT).show()
            mainContract.moveToDetail(data.id!!,data.photoUrl!!,data.description!!,data.name!!)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(data: List<DetailStory>){
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }


}