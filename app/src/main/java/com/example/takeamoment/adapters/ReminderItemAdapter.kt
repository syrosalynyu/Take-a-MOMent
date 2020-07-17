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

    private lateinit var mListener: OnItemClickListener

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
        val view = LayoutInflater.from(context).inflate(
                R.layout.item_reminder,
                parent,
                false
            )
        return MyViewHolder(view, mListener)
    }

    // (Retired) Was used for the swipetodelete function
//    fun deleteItem(position: Int){
//        list.removeAt(position)
//        notifyItemRemoved(position)
//    }

    interface OnItemClickListener{
        fun setOnClickListener(pos: Int)
    }

    fun setOnItemClickListener(mListener: OnItemClickListener){
        this.mListener = mListener
    }

    class MyViewHolder(view: View, var mListener: OnItemClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener{
        init{
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(mListener != null){
                mListener.setOnClickListener(adapterPosition)
            }
        }
    }
}

