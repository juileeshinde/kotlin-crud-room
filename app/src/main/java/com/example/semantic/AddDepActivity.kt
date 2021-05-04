package com.example.semantic

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semantic.databinding.DialogUpdateDependantBinding
import com.example.semantic.databinding.DisplaydataBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddDepActivity : AppCompatActivity() {
    private lateinit var binding: DisplaydataBinding
     var mobile =""
    var cal = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DisplaydataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "Add Relative"
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.homecreen_userdetails_bg_color_start1)))
        mobile = intent.getStringExtra("mobileNumber")
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
        binding.tvDOB.setOnClickListener {
            DatePickerDialog(this@AddDepActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        // Click even of the add button.
        binding.btnAdd.setOnClickListener { view ->

            addRecord()
        }
        setupListofDataIntoRecyclerView()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.tvDOB.text = sdf.format(cal.getTime())
    }
    /**
     * Function is used to show the list on UI of inserted data.
     */
    private fun setupListofDataIntoRecyclerView() {

        if (getItemsList().size > 0) {

            binding.rvItemsList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            binding.rvItemsList.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = DepItemAdapter(this, getItemsList())
            // adapter instance is set to the recyclerview to inflate the items.
            binding.rvItemsList.adapter = itemAdapter
        } else {

            binding.rvItemsList.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }
    //Method for saving the employee records in database
    private fun addRecord() {
        val name = binding.etName.text.toString()
        val dob = binding.tvDOB.text.toString()
        val relation = binding.etRelation.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (!name.isEmpty() && !dob.isEmpty() && !relation.isEmpty()) {
            val status =
                databaseHandler.addDependant(DependantModelClass(0, name,mobile,dob,relation))
            if (status > -1) {
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                binding.etName.text.clear()
                binding.tvDOB.text = ""
                binding.etRelation.text.clear()

             var intent = Intent(this@AddDepActivity, MainActivity::class.java)
                intent.putExtra("mobileNumber", mobile)
                intent.putExtra("name", name)
                startActivity(intent)
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Fields cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    /**
     * Function is used to get the Items List which is added in the database table.
     */
    private fun getItemsList(): ArrayList<DependantModelClass> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val dependantList: ArrayList<DependantModelClass> = databaseHandler.viewDependant(mobile)

        return dependantList
    }
    /**
     * Method is used to show the custom update dialog.
     */
    fun updateRecordDialog(dependant: DependantModelClass) {
      val li = LayoutInflater.from(this)
        val dialogBinding = DialogUpdateDependantBinding.inflate(li)
        val updateDialog = AlertDialog.Builder(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        updateDialog.setView(dialogBinding.root)

        dialogBinding.etUpdateName.setText(dependant.dependantName)
        dialogBinding.tvUpdateDOB.setText(dependant.dependantDOB)
        dialogBinding.etUpdateRelation.setText(dependant.dependantRelation)

        val alertDialog = updateDialog.create()

        dialogBinding.tvUpdate.setOnClickListener(View.OnClickListener {

            val name = dialogBinding.etUpdateName.text.toString()
            val dob = dialogBinding.tvUpdateDOB.text.toString()
            val relation = dialogBinding.etUpdateRelation.text.toString()

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            if (!name.isEmpty() && !dob.isEmpty() && !relation.isEmpty()) {
                val status =
                    databaseHandler.updateDependant(DependantModelClass(dependant.dependantId, name,mobile, dob,relation))
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

    }
    /**
     * Method is used to show the delete alert dialog.
     */
    fun deleteRecordAlertDialog(dependant: DependantModelClass) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${dependant.dependantName}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteDependant(DependantModelClass(dependant.dependantId, "","", "",""))
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
}