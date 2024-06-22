package com.dicoding.picodiploma.loginwithanimation

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainContract
import com.squareup.picasso.Picasso

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_item_name = itemView.findViewById<TextView>(R.id.title)
    val avatar = itemView.findViewById<ImageView>(R.id.img_item_photo)
    val card_view = itemView.findViewById<CardView>(R.id.card_view)


    fun bind(data: DetailStory?,context: Context,mainContract: MainContract){
        tv_item_name.text = data?.name
        Picasso.get().load(data?.photoUrl).into(avatar)
        card_view.setOnClickListener{
            Toast.makeText(context, "" + data?.id, Toast.LENGTH_SHORT).show()
            mainContract .moveToDetail(data?.id!!,data.photoUrl!!,data.description!!,data.name!!)
        }
    }

}