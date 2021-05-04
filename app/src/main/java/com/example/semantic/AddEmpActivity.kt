package com.example.semantic

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semantic.databinding.AddEmployeeScreenBinding
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

class AddEmpActivity : AppCompatActivity() {
    private lateinit var binding: AddEmployeeScreenBinding
    private lateinit var mobile : String
    private lateinit var userMobile : String
    private lateinit var visibility : String
    var cal = Calendar.getInstance()
    private lateinit  var radioButton : RadioButton
    val db: DatabaseHandler = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEmployeeScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "ADD EMPLOYEE"
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.homecreen_userdetails_bg_color_start1)))
        mobile = intent.getStringExtra("mobileNumber")
        visibility = intent.getStringExtra("visibility")
        if(visibility == "visible") {
            binding.etMobile.visibility = View.VISIBLE
            binding.tvMobile.visibility = View.GONE
            userMobile = intent.getStringExtra("mobileNumber")
        }
        else {
            binding.etMobile.visibility = View.GONE
            binding.tvMobile.visibility = View.VISIBLE
            binding.tvMobile.text = mobile
        }
        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }
        binding.etDOB.setOnClickListener {
            DatePickerDialog(this@AddEmpActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.btnAdd.setOnClickListener(){
            addRecord()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDOB.text = sdf.format(cal.getTime())
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        binding.tvAge.text = calculateAgeFromDOB(y,m,d).toString()
    }
    /***
     *Method for saving the employee records in database
     */
    private fun addRecord() {
        if(visibility=="visible")
            mobile = binding.etMobile.text.toString()
        val name = binding.etName.text.toString()
        val email = binding.etEmailId.text.toString()
        val dob = binding.etDOB.text.toString()
        val age = binding.tvAge.text.toString()
        val designation = binding.etDes.text.toString()
        val dept = binding.etDept.text.toString()
        val selectedOption: Int = binding.RGgender.checkedRadioButtonId
        radioButton = findViewById(selectedOption)
        var gender = radioButton.text.toString()

        if (!name.isEmpty() && !mobile.isEmpty() && !email.isEmpty() && !dob.isEmpty() && !age.isEmpty() && !gender.isEmpty() && !designation.isEmpty() && !dept.isEmpty()) {
            val status = db.addEmployee(EmpModelClass(0, name,mobile, email,dob,age.toInt(),gender,designation,dept))
            if (status > -1) {
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                binding.etName.text.clear()
                binding.etEmailId.text.clear()
                binding.tvMobile.text =""
                binding.etDOB.text=""
                binding.tvAge.text=""
                binding.etDes.text.clear()
                binding.etDept.text.clear()
                binding.rbFemale.isChecked = false
                binding.rbMale.isChecked = false
                binding.rbTrans.isChecked = false
                if(visibility=="visible"){
                    var intent = Intent(this@AddEmpActivity, OTPActivity::class.java)
                    intent.putExtra("mobileNumber",userMobile)
                    intent.putExtra("name", name)
                    intent.putExtra("regStatus","done")
                    startActivity( intent)
                }else{
                    var intent = Intent(this@AddEmpActivity, MainActivity::class.java)
                    intent.putExtra("mobileNumber",mobile)
                    intent.putExtra("name", name)
                    startActivity( intent)
                }

            }
            else if(status == -2)
                Toast.makeText(applicationContext, "This mobile number is already registered. Please try again with different mobile number", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, "Fields cannot be blank", Toast.LENGTH_LONG).show()
        }
    }
   /* *//**
     * Method is used to show the custom update dialog.
     *//*
    fun updateRecordDialog(empModelClass: EmpModelClass) {
        val li = LayoutInflater.from(this)
        val dialogBinding = DialogUpdateBinding.inflate(li)
        val updateDialog = AlertDialog.Builder(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        *//*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*//*
        updateDialog.setView(dialogBinding.root)

        dialogBinding.etUpdateName.setText(empModelClass.name)
        dialogBinding.etUpdateEmailId.setText(empModelClass.email)

        val alertDialog = updateDialog.create()

        dialogBinding.tvUpdate.setOnClickListener(View.OnClickListener {

            val name = dialogBinding.etUpdateName.text.toString()
            val email = dialogBinding.etUpdateEmailId.text.toString()

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            if (!name.isEmpty() && !email.isEmpty()) {
                val status =
                        databaseHandler.updateEmployee(EmpModelClass(empModelClass.id, name, email))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG).show()

                    setupListofDataIntoRecyclerView()
                }
            } else {
                Toast.makeText(
                        applicationContext,
                        "Name or Email cannot be blank",
                        Toast.LENGTH_LONG
                ).show()
            }
        })
        dialogBinding.tvCancel.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })
        alertDialog.show()

    }*/
    /**
     * Method is used to show the delete alert dialog.
     */
    fun deleteRecordAlertDialog(empModelClass: EmpModelClass) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${empModelClass.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteEmployee(EmpModelClass(empModelClass.id, "", "","","",0,"","",""))
            if (status > -1) {
                Toast.makeText(
                        applicationContext,
                        "Record deleted successfully.",
                        Toast.LENGTH_LONG
                ).show()

                setupListofDataIntoRecyclerView()
            }

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAgeFromDOB(year : Int, month : Int, dayOfMonth : Int): Int {
      return  Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
        ).years
    }
    /**
     * Function is used to show the list on UI of inserted data.
     */
    /**
     * Function is used to get the Items List which is added in the database table.
     */
    private fun getItemsList(): ArrayList<EmpModelClass> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val empList: ArrayList<EmpModelClass> = databaseHandler.viewEmployee(mobile)

        return empList
    }
    private fun setupListofDataIntoRecyclerView() {

        /*if (getItemsList().size > 0) {

            binding.rvItemsList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            binding.rvItemsList.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = EmpItemAdapter(this, getItemsList())
            // adapter instance is set to the recyclerview to inflate the items.
            binding.rvItemsList.adapter = itemAdapter
        } else {

            binding.rvItemsList.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }*/
    }
}