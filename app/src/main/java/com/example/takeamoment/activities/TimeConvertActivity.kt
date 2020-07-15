package com.example.takeamoment.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.takeamoment.R
import com.example.takeamoment.firebase.FirestoreClass
import com.example.takeamoment.models.Reminder
import com.example.takeamoment.receivers.MyBroadcastReceiver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_time_convert.*
import kotlinx.android.synthetic.main.activity_time_convert.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class TimeConvertActivity : AppCompatActivity() {

    companion object{
        const val SET_ALARM_REQUEST_CODE: Int = 12
    }

    private lateinit var mName: String
    private lateinit var mTimezone: String
    private lateinit var momName: String
    private lateinit var momTimezone: String

    private var currentUnix: Long = 0
    private var futureUnix: Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_convert)


        // showinfo()

        // disable the keyboard for the EditText fields
        et_my_datetime.inputType = InputType.TYPE_NULL
        et_mom_datetime.inputType = InputType.TYPE_NULL

        // to get the putExtra info from the MainActivity
        if (intent.hasExtra("name")) {
            mName = intent.getStringExtra("name")
        }
        if (intent.hasExtra("my_timezone")) {
            mTimezone = intent.getStringExtra("my_timezone")
        }
        if (intent.hasExtra("mom_name")) {
            momName = intent.getStringExtra("mom_name")
        }
        if (intent.hasExtra("mom_timezone")) {
            momTimezone = intent.getStringExtra("mom_timezone")
        }

        showInfo()

        btn_my_datetime_picker.setOnClickListener {
            clickMyDateTimePicker()
        }

        btn_mom_datetime_picker.setOnClickListener {
            clickMomDateTimePicker()
        }

        btn_create_reminder.setOnClickListener {
            createReminder()

            // get the current system UNIX time
            currentUnix = System.currentTimeMillis() / 1000L
            // get the future UNIX from the top
            // futureUnix - current UNIX
            var sec = (futureUnix - currentUnix).toInt()
            var intent = Intent(applicationContext, MyBroadcastReceiver::class.java)
            var pi = PendingIntent.getBroadcast(applicationContext, SET_ALARM_REQUEST_CODE, intent, 0)

            var am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (sec*1000), pi)
            Toast.makeText(applicationContext, "Alarm is set for ${currentUnix}, ${futureUnix}", Toast.LENGTH_LONG).show()
        }
    }

    // To create the board document on Firestore
    private fun createReminder() {
        val user: String = FirestoreClass().getCurrentUserId()
        val myDateTime: String = et_my_datetime.text.toString().trim { it <= ' ' }
        val momDateTime: String = et_mom_datetime.text.toString().trim { it <= ' ' }

        // create a Reminder object locally
        var reminder = Reminder(user, myDateTime, momName, momDateTime)

        // do the actually creation on Firestore
        FirestoreClass().createReminderOnFirestore(this, reminder)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // TODO: have to verify if it works?
    private fun showInfo() {
        tv_my_name.text = mName
        tv_my_timezone.text = mTimezone
        tv_mom_name.text = momName
        tv_mom_timezone.text = momTimezone
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
        // Log.i("AT MainActivity", "DocumentSnapshot data: $document $userID")
        // Toast.makeText(this, document.data.toString(), Toast.LENGTH_LONG).show()

        // to retrieve the user data from Cloud Firestore => will later use at the TimeConvertActivity
        // testing_user_profile.text = document.data!!["name"].toString() + document.data!!["email"].toString() + document.data!!["userTimeZone"].toString() + document.data!!["momName"].toString() + document.data!!["momTimezone"].toString()
        // tv_username.text = document.data!!["name"].toString()
//            tv_my_name.text = "${document.data!!["name"].toString()}'s Timezone:"
//            tv_my_timezone.text = document.data!!["userTimezone"].toString()
//            tv_mom_name.text = "${document.data!!["momName"].toString()}'s Timezone"
//            tv_mom_timezone.text = document.data!!["momTimezone"].toString()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickMyDateTimePicker() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDate = myCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = myCalendar.get(Calendar.MINUTE)
        val is24HourView = false

        // call the date picker
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, selectedYear: Int, selectedMonth, selectedDayOfMonth ->
                TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view, selectedHourOfDay, selectedMinute ->

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        val myDateTime = LocalDateTime.of(
                            selectedYear,
                            selectedMonth + 1,
                            selectedDayOfMonth,
                            selectedHourOfDay,
                            selectedMinute
                        );
                        // val myFormattedDateTime = myDateTime.format(formatter);

                        val myTimeZone = ZoneId.of("America/Los_Angeles")
                        val momTimeZone = ZoneId.of("Asia/Taipei");

                        // https://stackoverflow.com/questions/42280454/changing-localdatetime-based-on-time-difference-in-current-time-zone-vs-eastern?rq=1#:~:text=To%20convert%20a%20LocalDateTime%20to,result%20back%20to%20a%20LocalDateTime%20.
                        val zonedDateTime =
                            myDateTime.atZone(myTimeZone).withZoneSameInstant(momTimeZone)

//                        tv_my_datetime.setText(myFormattedDateTime.toString())
//                        tv_mom_datetime.setText(zonedDateTime.format(formatter))
                        et_my_datetime.setText(myDateTime.format(formatter))
                        et_mom_datetime.setText(zonedDateTime.format(formatter))

                        // get the future time in UNIX format
                        val epoch: Long = myDateTime.atZone(myTimeZone).toEpochSecond()
                        futureUnix = epoch

                    }, hourOfDate
                    , minute
                    , is24HourView
                ).show()

            }, year
            , month
            , day
        ).show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickMomDateTimePicker() {

        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDate = myCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = myCalendar.get(Calendar.MINUTE)
        val is24HourView = false

        // call the date picker
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, selectedYear: Int, selectedMonth, selectedDayOfMonth ->
                TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view, selectedHourOfDay, selectedMinute ->

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        val myDateTime = LocalDateTime.of(
                            selectedYear,
                            selectedMonth + 1,
                            selectedDayOfMonth,
                            selectedHourOfDay,
                            selectedMinute
                        );
                        // val myFormattedDateTime = myDateTime.format(formatter);

                        // https://howtodoinjava.com/java/date-time/localdate-zoneddatetime-conversion/
                        val myTimeZone = ZoneId.of("America/Los_Angeles")
                        val momTimeZone = ZoneId.of("Asia/Taipei");

                        val zdtAtMom = myDateTime.atZone(momTimeZone)
                        val zdtAtMy = zdtAtMom.withZoneSameInstant(myTimeZone)

//                        tv_my_datetime.setText(zdtAtMy.format(formatter))
//                        tv_mom_datetime.setText(zdtAtMom.format(formatter))
                        et_my_datetime.setText(zdtAtMy.format(formatter))
                        et_mom_datetime.setText(zdtAtMom.format(formatter))

                        // get the future time in UNIX format
                        val epoch: Long = myDateTime.atZone(momTimeZone).toEpochSecond()
                        futureUnix = epoch

                    }, hourOfDate
                    , minute
                    , is24HourView
                ).show()

            }, year
            , month
            , day
        ).show()
    }

    fun reminderCreatedSuccessfully(){
        // In order to add the new created reminder to the homepage,
        // we have to set it to RESULT_OK
        setResult(Activity.RESULT_OK)
        finish()
    }
}


