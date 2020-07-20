package com.example.takeamoment.activities

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.takeamoment.R
import com.example.takeamoment.adapters.ReminderItemAdapter
import com.example.takeamoment.firebase.FirestoreClass
import com.example.takeamoment.models.Reminder
import com.example.takeamoment.models.User
import com.example.takeamoment.receivers.MyBroadcastReceiver
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
        const val MY_PROFILE_REQUEST_CODE: Int = 8
        const val CREATE_REMINDER_REQUEST_CODE: Int = 10
    }

    private lateinit var mName: String
    private lateinit var mTimezone: String
    private lateinit var momName: String
    private lateinit var momTimezone: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        nav_view.setNavigationItemSelectedListener(this)

        // call the FirestoreClass().signInUser() to determine what to do
        FirestoreClass().signInUser(this, true)

        fab_create_reminder.setOnClickListener{
            val intentOne = Intent(this, TimeConvertActivity::class.java)
            // I also want to pass extra info to the TimeConvertActivity
            intentOne.putExtra("name", mName)
            intentOne.putExtra("my_timezone", mTimezone)
            intentOne.putExtra("mom_name", momName)
            intentOne.putExtra("mom_timezone", momTimezone)

            startActivityForResult(intentOne, CREATE_REMINDER_REQUEST_CODE)

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
        // if the drawer is open, we want to close it
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Add the click events of navigation menu items
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }
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


    // Create the board on the MainActivity
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

            // Click to delete the existing reminder
            adaptor.setOnItemClickListener(object: ReminderItemAdapter.OnItemClickListener{
                override fun setOnClickListener(pos: Int) {

                    val reminder = remindersList[pos]
                    val myFutureUnix = reminder.myFutureUnix.toInt()
                    cancelAlarm(myFutureUnix)

                    FirestoreClass().deleteReminderOnFirestore(this@MainActivity, reminder)

                    // remove it from the RecyclerView
                    remindersList.removeAt(pos)
                    adaptor.notifyItemRemoved(pos)
                }

            })

            // (Retired) Replace the SwipeToDelete with the OnItemClick
            // var itemTouchHelper: ItemTouchHelper = ItemTouchHelper(SwipeToDelete(adaptor))
            // itemTouchHelper.attachToRecyclerView(rv_reminders_list)
        }else{
            rv_reminders_list.visibility = View.GONE
            tv_no_reminders_available.visibility = View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirestoreClass().signInUser(this@MainActivity)
        }

        if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_REMINDER_REQUEST_CODE
        ) {
            // Get the latest reminders list from Firestore
            FirestoreClass().getRemindersList(this)

        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }


    private fun cancelAlarm(myfutureUnix: Int){

        val intent = Intent(applicationContext, MyBroadcastReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            applicationContext,
            myfutureUnix,
            intent,
            PendingIntent.FLAG_ONE_SHOT)
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.cancel(pi)
        Toast.makeText(applicationContext, "Alarm is now cancelled.", Toast.LENGTH_LONG).show()

    }

}