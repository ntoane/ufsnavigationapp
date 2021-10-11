package com.example.ufsnavigationassistant.core

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.models.BuildingLevel
import kotlinx.android.synthetic.main.building_level_list_item.view.*

class BuildingLevelAdapter(
    private val levelList: List<BuildingLevel>,
    private val listener: BuildingLevelAdapter.OnItemClickListener
) : RecyclerView.Adapter<BuildingLevelAdapter.BuildingLevelViewHolder>() {

    // this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingLevelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.building_level_list_item, parent, false)
        return BuildingLevelViewHolder(itemView)
    }

    // this method is binding the data on the list
    override fun onBindViewHolder(holder: BuildingLevelViewHolder, position: Int) {
        val currentItem = levelList[position]
        holder.levelName.text = "Level " + currentItem.floor_num.toString()
        holder.numRooms.text = "Number of rooms " + currentItem.num_room.toString()
    }

    // this method is returning the size of the list
    override fun getItemCount(): Int {
        return levelList.size
    }

    // the class is holding the listview
    inner class BuildingLevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val levelName: TextView = itemView.tv_building_level
        val numRooms: TextView = itemView.tv_num_room

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val data = levelList[position]
                listener.onItemClick(data)
            }
        }
    }
    //This interface can be implemented by any activity
    interface OnItemClickListener {
        fun onItemClick(buildingLevel: BuildingLevel)
    }
}