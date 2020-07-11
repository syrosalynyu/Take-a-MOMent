package com.example.takeamoment.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.takeamoment.R
import com.example.takeamoment.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_sign_in_intro.setOnClickListener {
            startActivity(Intent(this, SignInActivity:: class.java))
        }

        btn_sign_up_intro.setOnClickListener {
            startActivity(Intent(this, SignUpActivity:: class.java))
        }

        // 這部分一直造成我的app閃退！！！
//        Handler().postDelayed({
//            // check if there is a user id, if so, auto login
//            var currentUserID = FirestoreClass().getCurrentUserId()
//            if(currentUserID.isNotEmpty()){
//                startActivity(Intent(this, MainActivity:: class.java))
//            }
//
//            // startActivity(Intent(this, IntroActivity:: class.java))
//            finish()
//        }, 1000)
    }
}