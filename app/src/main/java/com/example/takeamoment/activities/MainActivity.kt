package com.example.takeamoment.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.takeamoment.R
import com.example.takeamoment.firebase.FirestoreClass
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        // Finally, we set the listener
        // this NavigationView id from avtivity_main
        nav_view.setNavigationItemSelectedListener(this)

        // call the FirestoreClass().signInUser() to determin what to do
        //  FirestoreClass().signInUser(this)
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
//            R.id.nav_my_profile -> {
//                //Toast.makeText(this@MainActivity, "My Profile", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, MyProfileActivity::class.java))
//            }

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
}