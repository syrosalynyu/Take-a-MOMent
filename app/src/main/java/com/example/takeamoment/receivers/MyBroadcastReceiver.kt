package com.example.takeamoment.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.content.ContextCompat.startActivity
import com.example.takeamoment.R
import com.example.takeamoment.activities.AlarmOnActivity

class MyBroadcastReceiver: BroadcastReceiver() {
    // what do you want the app to do at the time you set for Broadcasting
    override fun onReceive(context: Context?, intent: Intent?) {
        // I want to play the alarm tone
        // create a instance of the MediaPlayer
        val intent = Intent(context, AlarmOnActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }
}