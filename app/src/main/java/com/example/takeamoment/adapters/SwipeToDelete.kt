package com.example.takeamoment.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.takeamoment.activities.MainActivity


// Replace this function using the OnItemClickListener. Item will be deleted when user click on the item.

//class SwipeToDelete(var adapter: ReminderItemAdapter): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//    override fun onMove(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ): Boolean {
//
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        var position = viewHolder.adapterPosition
//        adapter.deleteItem(position)
//
//    }
//
//}