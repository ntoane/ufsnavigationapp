package com.example.ufsnavigationassistant.core

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.models.LevelRoom
import kotlinx.android.synthetic.main.building_room_list_item.view.*

class LevelRoomAdapter(
    private val roomList: List<LevelRoom>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LevelRoomAdapter.LevelRoomViewHolder>() {
    // this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelRoomViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.building_room_list_item, parent, false)
        return LevelRoomViewHolder(itemView)
    }

    // this method is binding the data on the list
    override fun onBindViewHolder(holder: LevelRoomViewHolder, position: Int) {
        val currentItem = roomList[position]
        holder.roomName.text = currentItem.room_name
        holder.buildingName.text = currentItem.building_name
    }

    // this method is returning the size of the list
    override fun getItemCount(): Int {
        return roomList.size
    }

    // the class is holding the listview
    inner class LevelRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val roomName: TextView = itemView.tv_room_name
        val buildingName: TextView = itemView.tv_building_name

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val data = roomList[position]
                listener.onItemClick(data)
                Log.d("Listener", "Listened")
            }
        }
    }
    //This interface can be implemented by any activity
    interface OnItemClickListener {
        fun onItemClick(room: LevelRoom)
    }
}