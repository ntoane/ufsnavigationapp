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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.building_list_item.view.*
import kotlinx.android.synthetic.main.event_list_item.view.*

class EventAdapter(
    private val eventList: List<Event>,
    private val listener: OnClickListener
    ) : RecyclerView.Adapter<EventAdapter.EventViewHolder>()
{
    // this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_list_item, parent, false)
        return EventViewHolder(itemView)
    }

    // this method is binding the data on the list
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentItem = eventList[position]

        holder.eventName.text = currentItem.event_name
        holder.eventBuilding.text = currentItem.building_name
        holder.eventDate.text = currentItem.event_date
        holder.startTime.text = currentItem.start_time
        holder.endTime.text = currentItem.end_time
    }

    // this method is returning the size of the list
    override fun getItemCount(): Int {
        return eventList.size
    }

    // the class is holding the listview
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val eventName: TextView = itemView.event_name
        val eventBuilding: TextView = itemView.event_building
        val eventDate: TextView = itemView.event_date
        val startTime: TextView = itemView.start_time
        val endTime: TextView = itemView.end_time

        init {
            itemView.event_start_nav.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val data = eventList[position]
                listener.onItemClick(data)
            }
        }
    }
    //This interfaces can be implemented by any activity
    interface OnClickListener {
        fun onItemClick(event: Event)
    }

}
