package com.example.takeamoment.firebase

import android.util.Log
import android.widget.Toast
import com.example.takeamoment.activities.MainActivity
import com.example.takeamoment.activities.TimeConvertActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreClass {

    private val mFireStore = Firebase.firestore

    fun registerUserOnFirestore(email: String, password: String, name: String,
                                userTimezone: String, momName: String, momTimezone: String){
        // create hash
        val user: HashMap<String, String> = hashMapOf(
            "email" to email,
            "password" to password,
            "name" to name,
            "userTimezone" to userTimezone,
            "momName" to momName,
            "momTimezone" to momTimezone)

        // pass the hash into the function call
        mFireStore.collection("users").document(getCurrentUserId())
            .set(user)
            .addOnSuccessListener {
                Log.d("At FirestoreClass", "DocumentSnapshot successfully written!")
            }.addOnFailureListener { e ->
                Log.w("At FirestoreClass", "Error writing document", e)
            }
    }

    fun createReminderOnFirestore(activity: TimeConvertActivity, user: String, myDateTime: String, momDateTime: String){
        // create hash
        val reminder: HashMap<String, String> = hashMapOf(
            "user" to user,
            "myDateTime" to myDateTime,
            "momDateTime" to momDateTime
            )

        // pass the hash into the function call
        mFireStore.collection("reminders").document()
            .set(reminder)
            .addOnSuccessListener {
                Log.e("At FirestoreClass", "Reminder successfully created.")
                Toast.makeText(activity, "Reminder created successfully", Toast.LENGTH_LONG).show()

                // call the function from the CreateBoardActivity
                //activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                    exception ->
                // activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Reminder creation failed.",
                    exception
                )
            }
    }


    fun getCurrentUserId(): String{
        // return FirebaseAuth.getInstance().currentUser!!.uid

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}