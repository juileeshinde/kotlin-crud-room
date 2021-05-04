package com.example.semantic

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.semantic.databinding.LoginScreenBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : LoginScreenBinding
    val db: DatabaseHandler = DatabaseHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.buttonLogin.setOnClickListener(){
      if(db?.checkUserAlreadyRegistered(binding.editMobileNumber.text.toString())==1) {
          var name = db.getUsername(binding.editMobileNumber.text.toString())
          val status = db.addLoginEntry(LoginModelClass(name,binding.editMobileNumber.text.toString()))
          if (status > -1) {
              Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_LONG).show()
          }
         var intent= Intent(this@LoginActivity, MainActivity::class.java)
          intent.putExtra("mobileNumber", binding.editMobileNumber.text.toString())
          intent.putExtra("name", name)
          startActivity(intent)
      } else
          Toast.makeText(applicationContext, "Looks like user is not registered.Please sign up first.", Toast.LENGTH_LONG).show()
        }
        binding.buttonSignup.setOnClickListener(){
            var intent = Intent(this@LoginActivity, OTPActivity::class.java)
            intent.putExtra("regStatus","not_done")
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        System.exit(1)
    }
}