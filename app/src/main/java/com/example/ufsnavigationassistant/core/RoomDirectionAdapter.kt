package com.example.ufsnavigationassistant.core

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.models.Event
import com.example.ufsnavigationassistant.models.RoomDirection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.building_list_item.view.*
import kotlinx.android.synthetic.main.event_list_item.view.*
import kotlinx.android.synthetic.main.room_direction_list.view.*

class RoomDirectionAdapter(
    private val directionList: List<RoomDirection>) : RecyclerView.Adapter<RoomDirectionAdapter.RoomDirectionViewHolder>() {
    // this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomDirectionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.room_direction_list, parent, false)
        return RoomDirectionViewHolder(itemView)
    }

    // this method is binding the data on the list
    override fun onBindViewHolder(holder: RoomDirectionViewHolder, position: Int) {
        val currentItem = directionList[position]

        holder.entrance.text = currentItem.entrance
        holder.directions.text = currentItem.directions
    }

    // this method is returning the size of the list
    override fun getItemCount(): Int {
        return directionList.size
    }

    // the class is holding the listview
    inner class RoomDirectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val entrance: TextView = itemView.entrance
        val directions: TextView = itemView.directions
    }

}
