package com.kacper.myweatherapp.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.kacper.myweatherapp.R
import com.kacper.myweatherapp.data.City

class MyCityRecyclerViewAdapter(
    private val activity: FragmentActivity?,
    private val values: List<City>,
    private var listener:  (City) -> Unit
) : RecyclerView.Adapter<MyCityRecyclerViewAdapter.ViewHolder>() {

    private val ITEM_VIEW = 0
    private val BUTTON_VIEW = 1
    private val log_tag = "RECYCLER VIEW"

    override fun getItemViewType(position: Int): Int {
        return if (position < values.size) {
            ITEM_VIEW;
        } else {
            BUTTON_VIEW;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lateinit var view : View
        when(viewType){
            ITEM_VIEW -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_city, parent, false)
            }
            BUTTON_VIEW -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_city_row, parent, false)
            }
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= values.size) {
            Log.d(log_tag, "Button-Position $position")
            holder.buttonView?.setOnClickListener {
                onAddCityButtonClick()
            }
        } else {
            Log.d(log_tag, "Item-Position $position")
            val item = values[position]
            val imageResourceId = holder.itemView.context.resources.getIdentifier(
                item.iconName, "drawable",
                holder.itemView.context.packageName
            )
            holder.imageView?.setImageResource(imageResourceId)
            holder.nameView?.text = item.name
            holder.temperatureView?.text = item.temperature.toString()
            holder.itemView.setOnClickListener { listener(item) }
        }
    }

    override fun getItemCount(): Int = values.size + 1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView? = view.findViewById(R.id.imageView_weather_list)
        val nameView: TextView? = view.findViewById(R.id.textView_list_name)
        val temperatureView: TextView? = view.findViewById(R.id.textView_list_temperature)
        val buttonView : Button? = view.findViewById(R.id.button_add_city_row)

        override fun toString(): String {
            return super.toString() + " '" + temperatureView?.text + "'"
        }
    }

    private fun onAddCityButtonClick(){
        val addCityFragment = NewCityFragment.newInstance("TODO","TODO")
        activity?.supportFragmentManager?.let { addCityFragment.show(it, "AddCityFragment") }
    }
}