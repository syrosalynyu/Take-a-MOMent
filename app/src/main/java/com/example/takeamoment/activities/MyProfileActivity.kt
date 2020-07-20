package com.example.takeamoment.activities

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.example.takeamoment.R
import com.example.takeamoment.firebase.FirestoreClass
import com.example.takeamoment.models.User
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {

    private lateinit var mUserDetails: User

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().signInUser(this, true)

        btn_update.setOnClickListener{
            updateUserProfileData()
        }
    }

    private fun setupActionBar(){
        // a Android function
        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            // set the arrow button that we want to use.
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.nav_my_profile)
        }
        // in order to make the button work
        toolbar_my_profile_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUserDataInUI(user: User){

        mUserDetails = user

        var yourTimezone = user.userTimezone
        var momTimezone = user.momTimezone

        // To display the pre-select the value stored in database for a Spinner
        // https://intellipaat.com/community/24431/how-to-set-selected-item-of-spinner-by-value-not-by-position
        var adapter = ArrayAdapter.createFromResource(this, R.array.timezone_list, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_your_timezone_update.setAdapter(adapter)
        if (yourTimezone != null) {
            var spinnerPosition = adapter.getPosition(yourTimezone);
            sp_your_timezone_update.setSelection(spinnerPosition);
        }
        sp_mom_timezone_update.setAdapter(adapter)
        if (momTimezone != null) {
            var spinnerPosition = adapter.getPosition(momTimezone);
            sp_mom_timezone_update.setSelection(spinnerPosition);
        }

        et_name_update.setText(user.name)
        et_email.setText(user.email)
        et_mom_name_update.setText(user.momName)
    }

    // to create the user HashMap
    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        var anyChangesMade = false

        if (et_name_update.text.toString() != mUserDetails.name) {
            userHashMap["name"] = et_name_update.text.toString()
            anyChangesMade = true
        }
        if (sp_your_timezone_update.selectedItem.toString() != mUserDetails.userTimezone) {
            userHashMap["userTimezone"] = sp_your_timezone_update.selectedItem.toString()
            anyChangesMade = true
        }
        if (et_mom_name_update.text.toString() != mUserDetails.momName) {
            userHashMap["momName"] = et_mom_name_update.text.toString()
            anyChangesMade = true
        }

        if (sp_mom_timezone_update.selectedItem.toString() != mUserDetails.momTimezone) {
            userHashMap["momTimezone"] = sp_mom_timezone_update.selectedItem.toString()
            anyChangesMade = true
        }
        // Update the data in the database.
        if(anyChangesMade){
            FirestoreClass().updateUserProfileData(this@MyProfileActivity, userHashMap)
        }
    }

    fun profileUpdateSuccess(){
        setResult(Activity.RESULT_OK)
        finish()
    }
}