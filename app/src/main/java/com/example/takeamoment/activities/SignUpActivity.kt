package com.example.takeamoment.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.takeamoment.R
import com.example.takeamoment.firebase.FirestoreClass
import com.example.takeamoment.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    // Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setupActionBar()

        // Initialize Firebase Auth
        auth = Firebase.auth

        // to prevent the screen scrolls back to the last EditText after choosing any spinner option
        sp_your_timezone.isFocusableInTouchMode = true
        sp_mom_timezone.isFocusableInTouchMode = true

        //CALL the registerUser function when clicking on the Sign Up button
        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    // to create a back button on the top left
    private fun setupActionBar(){
        // a Android function
        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            // set the arrow button that we want to use.
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        // in order to make the button work
        toolbar_sign_up_activity.setNavigationOnClickListener{ onBackPressed()}
    }


    // To register a user on Firebase Auth
    private fun registerUser(){
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val userTimeZone: String = sp_your_timezone.selectedItem.toString().trim { it <= ' ' }
        val momName: String = et_mom_name.text.toString().trim { it <= ' ' }
        val momTimezone: String = sp_mom_timezone.selectedItem.toString().trim { it <= ' ' }

        if (validateForm(email, password, name, userTimeZone, momName, momTimezone)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!

                        val user = User(
                            firebaseUser.uid,
                            registeredEmail,
                            name,
                            userTimeZone,
                            momName,
                            momTimezone
                        )

                        // Create the user document on Firestore once the auth succeed
                        FirestoreClass().registerUserOnFirestore(this, user)

                        Log.d("AT SignUpActivity", "createUserWithEmail:success")
                        Toast.makeText(
                            this, "You have successfully registered",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Log.w("AT SignUpActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String, name: String, userTimeZone: String, momName: String, momTimezone: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(baseContext, "Please enter email.", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(baseContext, "Please enter password.", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(name) -> {
                Toast.makeText(baseContext, "Please enter your name.", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(userTimeZone) -> {
                Toast.makeText(baseContext, "Please pick your timezone.", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(momName) -> {
                Toast.makeText(baseContext, "Please enter your mom's name.", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(momTimezone) -> {
                Toast.makeText(baseContext, "Please pick your mom's timezone.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }
}