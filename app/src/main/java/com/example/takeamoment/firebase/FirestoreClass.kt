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


    // To sign in the User (to get the user data from the Firebase)
    fun signInUser(activity: Activity, readRemindersList: Boolean = false){
        mFireStore.collection("users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document  ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
//                    is SignInActivity -> {
//                        activity.signInSuccess(loggedInUser)
//                    }
                    // 這個是要用來將傳送user info & readboardlist)
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readRemindersList)
                    }
                }

            }.addOnFailureListener{ e ->
                Log.e("SingInUser", "Error writing document", e)
            }
    }


    fun getCurrentUserId(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun createReminderOnFirestore(activity: TimeConvertActivity, reminder: Reminder){
        mFireStore.collection("reminders").document(reminder.myFutureUnix.toString())
            .set(reminder, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("At FirestoreClass", "Reminder successfully created.")
                activity.reminderCreatedSuccessfully()

            }.addOnFailureListener {
                    exception ->
                Log.e(activity.javaClass.simpleName, "Reminder creation failed.", exception)
            }
    }

    // To download the list of reminder documents from the Firestore
    fun getRemindersList(activity: MainActivity){
        mFireStore.collection("reminders")
            .whereEqualTo("user", getCurrentUserId())
            .orderBy("myDateTime")
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val remindersList = ArrayList<Reminder>()

                // go through the whole document, and add each of them to the remindersList
                for(i in document.documents){
                    val reminder = i.toObject(Reminder::class.java)!!
                    reminder.documentId = i.id
                    remindersList.add(reminder)
                }
                // now we can populate the sorted list of reminders on MainActivity
                activity.populateRemindersListToUI(remindersList)

            }.addOnFailureListener {e ->
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)

            }
    }

    fun deleteReminderOnFirestore(activity: MainActivity, reminder: Reminder){
        mFireStore.collection("reminders").document(reminder.myFutureUnix.toString())
            .delete()
            .addOnSuccessListener {
                    Log.d("At FirestoreClass", "DocumentSnapshot successfully deleted! ${reminder.myFutureUnix.toString()}")
            }.addOnFailureListener {
                    e -> Log.w("At FirestoreClass", "Error deleting document", e)
            }
    }
}