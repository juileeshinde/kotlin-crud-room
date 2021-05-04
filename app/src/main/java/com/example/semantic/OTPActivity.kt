package com.example.semantic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.semantic.databinding.ActivityOptScreenBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.installations.Utils
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var binding : ActivityOptScreenBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var anim : Animation
    lateinit var phoneNumber : String
    private lateinit var regStatus : String
    val db: DatabaseHandler = DatabaseHandler(this)
    companion object{
        private const val TAG = "MainActivity"
    }
    var mobile : String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "VERIFY NUMBER"
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.homecreen_userdetails_bg_color_start1)))

        regStatus = intent.getStringExtra("regStatus")

        anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_effect)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
             //   signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    binding.editMobileNumber.error = "Invalid phone number."
                } else if (e is FirebaseTooManyRequestsException) {

                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show()
                }
            }
           override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
            }
        }

        binding.btnNext.setOnClickListener {
            if(regStatus=="done"){
                var intent = Intent(this@OTPActivity, AddEmpActivity::class.java)
                intent.putExtra("mobileNumber", binding.editMobileNumber.text.toString())
                intent.putExtra("visibility", "gone")
                startActivity(intent)
            }else {
                var intent = Intent(this@OTPActivity, AddEmpActivity::class.java)
                intent.putExtra("mobileNumber", binding.editMobileNumber.text.toString())
                intent.putExtra("visibility", "gone")
                startActivity(intent)
            }
        }
        auth = Firebase.auth
        binding.btnSendOtp.setOnClickListener(this)
        binding.btnVerify.setOnClickListener(this)
    }
    private fun validatePhoneNumber(): Boolean {
         phoneNumber =binding.editMobileNumber.text.toString()
        var existsStatus = db.getUsername(phoneNumber)
        if(existsStatus.isNotEmpty() && existsStatus != "nullVal") {
            Toast.makeText(applicationContext, "This mobile number is already registered. Please try again with different mobile number.", Toast.LENGTH_LONG).show()
            return false
        } else {
            phoneNumber = "+91$phoneNumber"
            if (TextUtils.isEmpty(phoneNumber)) {
                binding.editMobileNumber.error = "Invalid phone number."
                return false
            }
        }
        return true
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                      //  Toast.makeText(this,"verifiva Successfull", Toast.LENGTH_SHORT).show()
                        binding.successfulImg.visibility = View.VISIBLE
                        binding.txtNextInfo.visibility = View.VISIBLE
                        binding.btnNext.visibility =View.VISIBLE
                        binding.txtSuccessfulmsg.visibility=View.VISIBLE
                        //set animation
                        binding.txtSuccessfulmsg.startAnimation(anim)
                        binding.txtNextInfo.startAnimation(anim)
                        binding.btnNext.startAnimation(anim)
                        val user = task.result?.user
                    } else {
                        binding.successfulImg.visibility = View.GONE
                        binding.txtNextInfo.visibility = View.GONE
                        binding.btnNext.visibility =View.GONE
                        binding.txtSuccessfulmsg.visibility=View.GONE
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            binding.etOTP.error = "Invalid code."
                        }
                    }
                }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnSendOtp->{
                if (!validatePhoneNumber()){
                    return
                }
                binding.etOTP.requestFocus()
                binding.btnSendOtp.text = "Resend OTP"
                startPhoneNumberVerification(phoneNumber)
            }
            R.id.btnVerify ->{
                val code = binding.etOTP.text.toString()
                if (TextUtils.isEmpty(code)) {
                    binding.etOTP.error = "Cannot be empty."
                    return
                }
                verifyPhoneNumberWithCode(storedVerificationId, code)
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        System.exit(1)
    }
}