package com.example.takeamoment.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.takeamoment.activities.MainActivity
import com.example.takeamoment.activities.SignInActivity
import com.example.takeamoment.activities.SignUpActivity
import com.example.takeamoment.activities.TimeConvertActivity
import com.example.takeamoment.models.Reminder
import com.example.takeamoment.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreClass {

    private val mFireStore = Firebase.firestore

    fun registerUserOnFirestore(activity: SignUpActivity, userInfo: User){
        mFireStore.collection("users").document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("At FirestoreClass", "DocumentSnapshot successfully written!")
            }.addOnFailureListener { e ->
                Log.w("At FirestoreClass", "Error writing document", e)
            }
    }

    fun createReminderOnFirestore(activity: TimeConvertActivity, reminder: Reminder){
        mFireStore.collection("reminders").document()
            .set(reminder, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("At FirestoreClass", "Reminder successfully created.")
                Toast.makeText(activity, "Reminder created successfully", Toast.LENGTH_LONG).show()

                // activity.reminderCreatedSuccessfully()
            }.addOnFailureListener {
                    exception ->
                Log.e(activity.javaClass.simpleName, "Reminder creation failed.", exception)
            }
    }

    // To sign in the User (to get the user data from the Firebase)
    fun signInUser(activity: Activity, readBoardsList: Boolean = false){
        mFireStore.collection("users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                // to store the log in user, and the document has all the info
                val loggedInUser = document.toObject(User::class.java)!!

                // OLD: now I can signin the user. This part only work for the SignInActivity
                // if(loggedInUser != null){
                // activity.signInSuccess(loggedInUser)
                // }
                when(activity){
//                    is SignInActivity -> {
//                        activity.signInSuccess(loggedInUser)
//                    }
                    // 這個是要用來將傳送user info & readboardlist)
                    is MainActivity -> {
                        // OLD: activity.updateNavigationUserDetails(loggedInUser)
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                }

            }.addOnFailureListener{ e ->
                Log.e("SingInUser", "Error writing document", e)
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