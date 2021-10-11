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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.building_list_item.view.*

class BuildingAdapter(
    private val buildingList: List<Building>,
    private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder>()
{
    // this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.building_list_item, parent, false)
        return BuildingViewHolder(itemView)
    }

    // this method is binding the data on the list
    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int) {
        val currentItem = buildingList[position]
        val firstImgUrl = "http://10.0.2.2/systems/ufsnavigation/uploads/buildings/" + currentItem.images.first().url
        //Log.d("Image url", firstImgUrl.toString())
        //Transform images to better fit into layouts and to reduce memory size.
        Picasso.get().load(firstImgUrl).resize(30,30).centerCrop().into(holder.buildingImageView)
        holder.buildingTextView.text = currentItem.building_name
    }

    // this method is returning the size of the list
    override fun getItemCount(): Int {
        return buildingList.size
    }

    // the class is holding the listview
    inner class BuildingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val buildingImageView: ImageView = itemView.building_image_view
        val buildingTextView: TextView = itemView.tv_building

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val data = buildingList[position]
                listener.onItemClick(data)
            }
        }
    }
    //This interface can be implemented by any activity
    interface OnItemClickListener {
        fun onItemClick(building: Building)
    }

}
