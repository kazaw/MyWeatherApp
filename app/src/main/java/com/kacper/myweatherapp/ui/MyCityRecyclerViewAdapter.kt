package com.kacper.myweatherapp.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kacper.myweatherapp.R
import com.kacper.myweatherapp.data.City

class MyCityRecyclerViewAdapter(
    private val values: List<City>,
    private var listener:  (City) -> Unit
) : RecyclerView.Adapter<MyCityRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val imageResourceId = holder.itemView.context.resources.getIdentifier(
            item.iconName, "drawable",
            holder.itemView.context.packageName
        )
        holder.imageView.setImageResource(imageResourceId)
        holder.nameView.text = item.name
        holder.temperatureView.text = item.temperature.toString()
        holder.itemView.setOnClickListener { listener(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView = view.findViewById(R.id.imageView_weather_list)
        val nameView: TextView = view.findViewById(R.id.textView_list_name)
        val temperatureView: TextView = view.findViewById(R.id.textView_list_temperature)

        override fun toString(): String {
            return super.toString() + " '" + temperatureView.text + "'"
        }
    }
}