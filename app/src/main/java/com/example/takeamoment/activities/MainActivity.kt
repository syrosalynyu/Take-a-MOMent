package com.example.takeamoment.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.takeamoment.R
import com.example.takeamoment.adapters.ReminderItemAdapter
import com.example.takeamoment.firebase.FirestoreClass
import com.example.takeamoment.models.Reminder
import com.example.takeamoment.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_time_convert.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    companion object{
        const val CREATE_REMINDER_REQUEST_CODE: Int = 10
    }

    private lateinit var mName: String
    private lateinit var mTimezone: String
    private lateinit var momName: String
    private lateinit var momTimezone: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        nav_view.setNavigationItemSelectedListener(this)

        // call the FirestoreClass().signInUser() to determine what to do
        FirestoreClass().signInUser(this, true)


        fab_create_reminder.setOnClickListener{
            val intent = Intent(this, TimeConvertActivity::class.java)

            // I also want to pass extra info to the TimeConvertActivity
            intent.putExtra("name", mName)
            intent.putExtra("my_timezone", mTimezone)
            intent.putExtra("mom_name", momName)
            intent.putExtra("mom_timezone", momTimezone)

            // startActivity()
            startActivityForResult(intent, CREATE_REMINDER_REQUEST_CODE)

        }
    }


    // set up the action bar to hold the hamburger icon
    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)

        // add the hamburger icon to the toolbar
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_hamburger_icon)

        // Add click event for navigation in the action bar and call the toggleDrawer function.)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        //drawer_layout is the DrawerLayput for the activity_main.xml
        // if it is open, we want to close it
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Add the click events of navigation menu items
        when (item.itemId) {
            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        // close the drawer no matter what
        drawer_layout.closeDrawer(GravityCompat.START)

        // the return type is Boolean, so we have to return
        return true
    }

    //
    fun updateNavigationUserDetails(user: User, readRemindersList: Boolean){
        // after doing the following, we can now pass these var to the TimeConvertActivity
        // using intent.putExtra() in the fab_create_reminder onClickListener
        mName = user.name
        mTimezone = user.userTimezone
        momName = user.momName
        momTimezone = user.momTimezone

        // to display the username in the Drawer
        tv_username.text = user.name

        // I wan to load the board if the readRemindersList is true
        if (readRemindersList){
            FirestoreClass().getRemindersList(this)
        }

    }

    // Create the board to the MainActivity
    // Note that, we haven't download them yet at this point
    // will have to create another function in the FirestoreClass to download the list
    fun populateRemindersListToUI(remindersList: ArrayList<Reminder>){

        if(remindersList.size > 0){
            rv_reminders_list.visibility = View.VISIBLE
            tv_no_reminders_available.visibility = View.GONE

            // what kind of layout manager I want to use
            rv_reminders_list.layoutManager = LinearLayoutManager(this)
            // make the boards list has a fixed size
            rv_reminders_list.setHasFixedSize(true)

            // prepare the adapter here
            val adaptor = ReminderItemAdapter(this, remindersList)
            // assign the adaptor to the rv_reminders_list
            rv_reminders_list.adapter = adaptor
        }else{
            rv_reminders_list.visibility = View.GONE
            tv_no_reminders_available.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_REMINDER_REQUEST_CODE
        ) {
            // Get the latest reminders list from Firestore
            FirestoreClass().getRemindersList(this)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

//    private fun showinfo(){
//        val userID = FirebaseAuth.getInstance().currentUser!!.uid
//        // 或許可以直接沿用 var currentUserID = FirestoreClass().getCurrentUserId() 來取得
//
//        val df = FirebaseFirestore.getInstance().collection("users").document(userID)
//        df.get().addOnSuccessListener {document ->
//            if (document != null) {
//                Log.i("AT MainActivity", "DocumentSnapshot data: ${document.data!!["name"]}")
//            } else {
//                Log.i("AT MainActivity", "No such document")
//            }
//            // Log.i("AT MainActivity", "DocumentSnapshot data: $document $userID")
//            // Toast.makeText(this, document.data.toString(), Toast.LENGTH_LONG).show()
//
//            // to retrieve the user data from Cloud Firestore => will later use at the TimeConvertActivity
//            // testing_user_profile.text = document.data!!["name"].toString() + document.data!!["email"].toString() + document.data!!["userTimeZone"].toString() + document.data!!["momName"].toString() + document.data!!["momTimezone"].toString()
//            tv_username.text = document.data!!["name"].toString()
//        }
//    }
}