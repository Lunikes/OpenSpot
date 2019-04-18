package com.example.openspot

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.regex.Pattern
import android.app.DatePickerDialog
import android.app.Dialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*


class userProfile : AppCompatActivity() {

    //UI elements
    private var editFullName: EditText? = null
    private var editEmail: EditText? = null
    private var editDOB: EditText? = null
    private var nextButton: ImageButton? = null
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser


//    //Firebase references
//    private var mDatabaseReference: DatabaseReference? = null
//    private var mDatabase: FirebaseDatabase? = null
//    private var mAuth: FirebaseAuth? = null

//    //global variables
//    private var fullName: String? = null
//    private var Email: String? = null
//    private var DOB: String? = null

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0

    private var mPickDate: Button? = null

    //findViewById(R.id.showMyDate) as TextView
    //findViewById(R.id.myDatePickerButton) as Button
    var DATE_DIALOG_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FragmentTheme)
        setContentView(R.layout.activity_user_profile)

        supportActionBar?.title = "User Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val docRef = db.collection("Users").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                //                    Toast.makeText(applicationContext, "" + user.uid, Toast.LENGTH_SHORT).show()
                if (document.exists()) {
                    val intent = Intent(this@userProfile, NavigationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        initialize()
    }

    private fun initialize() {
        editFullName = findViewById<View>(R.id.full_name) as EditText
        editEmail = findViewById<View>(R.id.email) as EditText
//      editDOB = findViewById<View>(R.id.dob) as EditText
        nextButton = findViewById<View>(R.id.user_profile_button) as ImageButton

//        mDatabase = FirebaseDatabase.getInstance()
//        mDatabaseReference = mDatabase!!.reference!!.child("Users")
//        mAuth = FirebaseAuth.getInstance()


        mPickDate = findViewById(R.id.myDatePickerButton)

//      mPickDate.setOnClickListener(View.OnClickListener { showDialog(DATE_DIALOG_ID) })

        // get the current date
        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

    }

    private fun updateDisplay() {
        this.mPickDate?.setText(
//            StringBuilder()
//                // Month is 0 based so add 1
//                .append(mMonth + 1).append("/")
//                .append(mDay).append("/")
//                .append(mYear).append(" ")
        dateAssembmly()
        )
    }

    private val mDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        mYear = year
        mMonth = monthOfYear + 1
        mDay = dayOfMonth
        updateDisplay()
    }

    override fun onCreateDialog(id: Int): Dialog? {
        super.onCreateDialog(id)
        when (id) {
            DATE_DIALOG_ID -> return DatePickerDialog(
                this,
                mDateSetListener,
                mYear, mMonth, mDay
            )
        }
        return null
    }

    private fun isValidName(name: String): Boolean {
        //checks to see if name doesn't have any special chars or numbers, if so, returns true

//method1
//        val inputStr = name
//        val pattern = Pattern.compile("/^[a-z ,.'-]+\$/i")
//        val matcher = pattern.matcher(inputStr)
//        return (matcher.matches())
//method2
//        val fullNameRegex = "/^[a-z ,.'-]+\$/i"
//        return fullNameRegex.toRegex().matches(name)
//brute method3
        val blockedChars = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '?', '.', ',', '/', '~', '`', '+', '-', '=', '*', '&', '^', '%', '$', '#', '@', '!', '(', ')', '|', '{', '}', '[', ']', ':', ';', '<', '>')
        for (char in blockedChars){
            if (name.contains(char)){
                return false
            }
        }
        return name.contains(" ")
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidDOB(): Boolean{
        if (mDay>31&&mDay<1){
            return false
        }
        if (mMonth>12&&mMonth<1){
            return false
        }
        if (Calendar.YEAR.minus(mYear)<15){
            Toast.makeText(applicationContext, "Must be old enough to drive!", Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }


    private fun validForm(): Boolean {
        val fullName = editFullName!!.text.toString()
        val email = editEmail!!.text.toString()
//        val dob = editDOB?.text.toString()

        //if they are empty
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please fill out all information", Toast.LENGTH_LONG)
                .show()
            return false
        } else {
            //if fields are not empty
            if (!isValidName(fullName)) {
                Toast.makeText(applicationContext, "Please enter valid Name", Toast.LENGTH_LONG)
                    .show()
                return false
            } else if(!isValidEmail(email)) {
                Toast.makeText(applicationContext, "Please enter valid Email", Toast.LENGTH_LONG)
                    .show()
                return false
            } else if(!isValidDOB()) {
                if (Calendar.YEAR.minus(mYear)<15) {
                    Toast.makeText(applicationContext, "Must be old enough to drive!", Toast.LENGTH_LONG)
                        .show()
                    return false
                } else {
                    Toast.makeText(applicationContext, "Please enter valid Birthday", Toast.LENGTH_LONG)
                        .show()
                    return false
                }
            } else {
                return true
            }
        }
    }


     fun datePicker(v: View){
        showDialog(DATE_DIALOG_ID)
    }

    fun dateAssembmly(): String{
        val sb = StringBuilder()
        if (mMonth<10){
            sb.append("0"+mMonth.toString()+"/")
        } else {
            sb.append(mMonth.toString()+"/")
        }
        if(mDay<10){
            sb.append("0"+mDay.toString()+"/")
        }else {
            sb.append(mDay.toString()+"/")
        }
        sb.append(mYear)
        return sb.toString()
    }

    fun nextButton(v:View) {
        if (validForm()) {
            val i = Intent(this@userProfile, VehicleInfoActivity::class.java)
            i.putExtra("fullName", editFullName!!.text.toString())
            i.putExtra("email", editEmail!!.text.toString())
            i.putExtra("dateOfBirth", dateAssembmly())
//            i.putExtra("DOB", editDOB!!.text.toString())

            //this code is for the next vehicle info activity to use these inputs
//            val extras = intent.extras
//            if (extras != null) {
//                val value = extras.getString("fullName")
//                val value = extras.getString("email")
//                val value = extras.getString("dateOfBirth")
//                //The key argument here must match that used in the other activity
//            }


            startActivity(i)
            finish()
        }
    }


}

