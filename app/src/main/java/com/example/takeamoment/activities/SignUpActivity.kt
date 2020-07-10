package com.example.takeamoment.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.takeamoment.R
import com.example.takeamoment.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    // Firebase Auth


    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //CALL the registerUser function when clicking on the Sign Up button
        btn_sign_up.setOnClickListener {view ->
            registerUser()
        }
    }

    // To register a user on Firebase Auth
    private fun registerUser(){
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        // TODO: create a signUpValidation() function,
        //  and validate before creating the user on Firebase Auth
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val userTimeZone: String = sp_your_timezone.selectedItem.toString().trim { it <= ' ' }
        val momName: String = et_mom_name.text.toString().trim { it <= ' ' }
        val momTimezone: String = sp_mom_timezone.selectedItem.toString().trim { it <= ' ' }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("AT SignUpActivity", "createUserWithEmail:success")
                    Toast.makeText(this, "You have successfully registered",
                        Toast.LENGTH_SHORT).show()

                    FirestoreClass().registerUserOnFirestore(email, password, name,
                        userTimeZone, momName, momTimezone)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w("AT SignUpActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}