package com.example.semantic

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semantic.databinding.ActivityMainBinding
import com.example.semantic.databinding.DialogUpdateDependantBinding


class MainActivity : AppCompatActivity(),View.OnClickListener {
    val db: DatabaseHandler = DatabaseHandler(this)
    private lateinit var binding : ActivityMainBinding
   var mobile =""
   var name =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "HOME"
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.homecreen_userdetails_bg_color_start1)))

        mobile = intent.getStringExtra("mobileNumber")
        name = intent.getStringExtra("name")
        binding.tvUserName.text = "Welcome $name"
        binding.btnAddDependant.setOnClickListener(this)
        binding.constraintNewEmp.setOnClickListener(this)
        binding.constraintUserProfile.setOnClickListener (this)
        binding.btnUserProfile.setOnClickListener(this)
        binding.btnAddNewEmp.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity,menu)
        var item = menu.findItem(R.id.action_logout)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_logout->{
                clearLoginTable()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
    fun clearLoginTable(){
        db.logOut()
    }
    /**
     * Function is used to get the Items List which is added in the database table.
     */
    private fun getItemsList(): ArrayList<DependantModelClass> {
        //creating the instance of DatabaseHandler class
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val dependantList: ArrayList<DependantModelClass> = db.viewDependant(mobile)

        return dependantList
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

    override fun onResume() {
        super.onResume()
        setupListofDataIntoRecyclerView()

    }
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
    override fun onBackPressed() {
        /* if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            collapseSheet();
            return;
        } else {*/
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("EXIT")
        alertDialogBuilder
            .setMessage("Do you want to exit?")
            .setCancelable(false)
            .setPositiveButton("yes")
             { dialog, id ->
                moveTaskToBack(true)
                Process.killProcess(Process.myPid())
                System.exit(1)
            }
            .setNegativeButton("no") { dialog, id -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        // }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnAddNewEmp->{
                var intent = Intent(this@MainActivity, AddEmpActivity::class.java)
                intent.putExtra("visibility","visible")
                intent.putExtra("mobileNumber",mobile)
                startActivity(intent)
            }
            R.id.btnUserProfile->{
                var intent = Intent(this@MainActivity, UserProfileActivity::class.java)
                intent.putExtra("mobileNumber",mobile)
                startActivity(intent)
            }
            R.id.constraint_new_emp->{
                var intent = Intent(this@MainActivity, AddEmpActivity::class.java)
                intent.putExtra("visibility","visible")
                intent.putExtra("mobileNumber",mobile)
                startActivity(intent)
            }
            R.id.constraint_user_profile->{
                var intent = Intent(this@MainActivity, UserProfileActivity::class.java)
                intent.putExtra("mobileNumber",mobile)
                startActivity(intent)
            }
            R.id.btnAddDependant->{
                var intent = Intent(this@MainActivity, AddDepActivity::class.java)
                intent.putExtra("mobileNumber",mobile)
                startActivity(intent)}
        }


    }
}