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
import com.example.ufsnavigationassistant.models.Timetable
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.building_list_item.view.*
import kotlinx.android.synthetic.main.event_list_item.view.*
import kotlinx.android.synthetic.main.event_list_item.view.event_start_nav
import kotlinx.android.synthetic.main.timetable_list_item.view.*

class TimetableAdapter(
    private val timetableList: List<Timetable>,
    private val listener: OnClickListener
    ) : RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>()
{
    // this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.timetable_list_item, parent, false)
        return TimetableViewHolder(itemView)
    }

    // this method is binding the data on the list
    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        val currentItem = timetableList[position]

        holder.courseName.text = currentItem.module_code
        holder.roomName.text = currentItem.room_name
        holder.buildingName.text = currentItem.building_name
        holder.day.text = currentItem.day
        holder.startTime.text = currentItem.start_time
        holder.endTime.text = currentItem.end_time
    }

    // this method is returning the size of the list
    override fun getItemCount(): Int {
        return timetableList.size
    }

    // the class is holding the listview
    inner class TimetableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val courseName: TextView = itemView.module_code
        val roomName: TextView = itemView.timetable_room
        val buildingName: TextView = itemView.timetable_building
        val day: TextView = itemView.timetable_day
        val startTime: TextView = itemView.timetable_start_time
        val endTime: TextView = itemView.timetable_end_time

        init {
            itemView.btn_delete_timetable.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val data = timetableList[position]
                listener.onItemClick(data)
            }
        }
    }
    //This interfaces can be implemented by any activity
    interface OnClickListener {
        fun onItemClick(timetable: Timetable)
    }
}
