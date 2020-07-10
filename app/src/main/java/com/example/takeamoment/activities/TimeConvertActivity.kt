package com.example.takeamoment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.takeamoment.R
import com.google.firebase.auth.FirebaseAuth

class TimeConvertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_convert)

        showinfo()
    }

    // need a funtion to retrieve the "name", "momName", "userTimezone", "momTimezone" from Cloud Firestore
    // ,then use them on the xml
    private fun showinfo(){
        FirebaseAuth.getInstance().currentUser
    }
}