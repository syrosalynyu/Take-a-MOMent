package com.example.takeamoment.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.takeamoment.R
import com.example.takeamoment.firebase.FirestoreClass
import com.example.takeamoment.models.Reminder
import com.example.takeamoment.receivers.MyBroadcastReceiver
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_time_convert.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class TimeConvertActivity : AppCompatActivity() {

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

        // disable the keyboard for the EditText fields
        et_my_datetime.inputType = InputType.TYPE_NULL
        et_mom_datetime.inputType = InputType.TYPE_NULL

        setupActionBar()

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

            if (futureUnix == 0L || futureUnix < currentUnix){
                Toast.makeText(applicationContext, "Cannot create a past reminder. \nPlease try again.", Toast.LENGTH_LONG).show()

            }else{
                setAlarm()

                createReminder()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupActionBar(){
        // a Android function
        setSupportActionBar(toolbar_time_convert_activity)
        val actionBar = supportActionBar

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_time_convert_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    // To create the board document on Firestore
    private fun createReminder() {
        val user: String = FirestoreClass().getCurrentUserId()
        val myDateTime: String = et_my_datetime.text.toString().trim { it <= ' ' }
        val momDateTime: String = et_mom_datetime.text.toString().trim { it <= ' ' }

        // create a Reminder object locally
        val reminder = Reminder(user, myDateTime, momName, momDateTime, "none", futureUnix)

        // do the actually creation on Firestore
        FirestoreClass().createReminderOnFirestore(this, reminder)


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(){
        // get the current system UNIX time
        currentUnix = System.currentTimeMillis() / 1000L
        // get the future UNIX from the top
        val sec = (futureUnix - currentUnix).toInt()
        val intent = Intent(applicationContext, MyBroadcastReceiver::class.java)

        val pi = PendingIntent.getBroadcast(applicationContext, futureUnix.toInt(), intent, PendingIntent.FLAG_ONE_SHOT)

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (sec*1000), pi)

    }


    private fun showInfo() {
        tv_my_name.text = mName
        tv_my_timezone.text = mTimezone
        tv_mom_name.text = momName
        tv_mom_timezone.text = momTimezone
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickMyDateTimePicker() {
        currentUnix = System.currentTimeMillis() / 1000L

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

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
                        val myDateTime = LocalDateTime.of(
                            selectedYear,
                            selectedMonth + 1,
                            selectedDayOfMonth,
                            selectedHourOfDay,
                            selectedMinute
                        )

                        val myTimeZone = ZoneId.of(mTimezone)
                        val momTimeZone = ZoneId.of(momTimezone)

                        // https://stackoverflow.com/questions/42280454/changing-localdatetime-based-on-time-difference-in-current-time-zone-vs-eastern?rq=1#:~:text=To%20convert%20a%20LocalDateTime%20to,result%20back%20to%20a%20LocalDateTime%20.
                        val zonedDateTime =
                            myDateTime.atZone(myTimeZone).withZoneSameInstant(momTimeZone)

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
        currentUnix = System.currentTimeMillis() / 1000L

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

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
                        val myDateTime = LocalDateTime.of(
                            selectedYear,
                            selectedMonth + 1,
                            selectedDayOfMonth,
                            selectedHourOfDay,
                            selectedMinute
                        )

                        // https://howtodoinjava.com/java/date-time/localdate-zoneddatetime-conversion/
                        val myTimeZone = ZoneId.of(mTimezone)
                        val momTimeZone = ZoneId.of(momTimezone)

                        val zdtAtMom = myDateTime.atZone(momTimeZone)
                        val zdtAtMy = zdtAtMom.withZoneSameInstant(myTimeZone)

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


