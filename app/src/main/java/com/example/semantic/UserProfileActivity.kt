package com.example.semantic

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.semantic.databinding.EmployeeProfileBinding
import java.util.ArrayList

class UserProfileActivity : AppCompatActivity() {
    lateinit var binding : EmployeeProfileBinding
    val db: DatabaseHandler = DatabaseHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EmployeeProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "EMPLOYEE PROFILE"
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.homecreen_userdetails_bg_color_start1)))

        var mobile = intent.getStringExtra("mobileNumber")
        val empList: ArrayList<EmpModelClass> = db.viewEmployee(mobile)
        binding.txtName.text = empList.get(0).name
        binding.txtPhone.text = empList.get(0).mobile
        binding.txtEmail.text = empList.get(0).email
        binding.txtAge.text = empList.get(0).age.toString()
        binding.txtDOB.text = empList.get(0).dob
        binding.txtGender.text = empList.get(0).gender
        binding.txtDes.text = empList.get(0).designation
        binding.txtDept.text = empList.get(0).dept

    }
}