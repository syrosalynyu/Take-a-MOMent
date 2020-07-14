package com.example.takeamoment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.takeamoment.R
import com.example.takeamoment.models.Reminder
import kotlinx.android.synthetic.main.item_reminder.view.*

open class ReminderItemAdapter(
    private val context: Context,
    private var list: ArrayList<Reminder>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_date_time.text = "🗓 ${model.myDateTime}"
            holder.itemView.tv_reminder_title.text = "Call ${model.momName}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_reminder,
                parent,
                false
            )
        )
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

