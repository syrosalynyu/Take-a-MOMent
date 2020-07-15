package com.example.takeamoment.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.takeamoment.R
import kotlinx.android.synthetic.main.activity_alarm_on.*


// Resource for set upt the alarm: https://www.youtube.com/watch?v=2ykRrMU1LPM&t=819s
class AlarmOnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_on)

        val mp = MediaPlayer.create(applicationContext, R.raw.lovingly)
        mp.start()

        btn_stop_alarm.setOnClickListener {
            mp.stop()
        }
    }
}