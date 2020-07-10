package com.example.takeamoment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.takeamoment.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // getUserProfile()
        showinfo()
    }

//    private fun getUserProfile(){
//        val user = Firebase.auth.currentUser
//        user?.let {
//            // Name, email address, and profile photo Url
//            val name = user.displayName
//            val email = user.email
//            val photoUrl = user.photoUrl
//
//            // Check if user's email is verified
//            val emailVerified = user.isEmailVerified
//
//            // The user's ID, unique to the Firebase project. Do NOT use this value to
//            // authenticate with your backend server, if you have one. Use
//            // FirebaseUser.getToken() instead.
//            val uid = user.uid
//
//            testing_user_profile.text = email + uid
//        }
//    }

    private fun showinfo(){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val df = FirebaseFirestore.getInstance().collection("users").document(userID)
        df.get().addOnSuccessListener {document ->
                Log.i("AT MainActivity", "DocumentSnapshot data: ${document.data}")
                // Toast.makeText(this, document.data.toString(), Toast.LENGTH_LONG).show()

            // to retrieve the user data from Cloud Firestore => will later use at the TimeConvertActivity
                testing_user_profile.text = document.data!!["name"].toString() + document.data!!["email"].toString() + document.data!!["userTimeZone"].toString() + document.data!!["momName"].toString() + document.data!!["momTimezone"].toString()
        }

    }
}