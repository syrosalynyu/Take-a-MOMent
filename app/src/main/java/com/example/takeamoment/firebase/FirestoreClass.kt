package com.example.takeamoment.firebase

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreClass {

    private val mFireStore = Firebase.firestore

    fun registerUserOnFirestore(email: String, password: String, name: String,
                                      userTimeZone: String, momName: String, momTimezone: String){
        // create hash
        val user: HashMap<String, String> = hashMapOf(
            "email" to email,
            "password" to password,
            "name" to name,
            "userTimeZone" to userTimeZone,
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