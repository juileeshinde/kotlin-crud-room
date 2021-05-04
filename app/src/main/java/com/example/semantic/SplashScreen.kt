package com.example.semantic

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashScreen : AppCompatActivity(){
    val db: DatabaseHandler = DatabaseHandler(this)
    lateinit var uName : String
    lateinit var uMobile : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        //declare animation
        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_down_effect)
        val anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_up_effect)

        var imgLogo = findViewById<ImageView>(R.id.logo)
        var imgRing =  findViewById<ImageView>(R.id.logoRing)
        var txtAppName =  findViewById<TextView>(R.id.txtAppName)

        //set animation
        imgLogo.startAnimation(anim)
        imgRing.startAnimation(anim1)
        txtAppName.startAnimation(anim1)
        Timer().schedule(2500)
        {
            checkIfUserAlreadyLoggedIn()
        }
    }
    fun checkIfUserAlreadyLoggedIn(){
        val loginDetails: ArrayList<LoginModelClass> = db.checkIfUserAlreadyLoggedIn()
        if(loginDetails.size>0) {
            uName = loginDetails.get(0).userName
            uMobile = loginDetails.get(0).userMobile
            if(!uName.isEmpty() && !uMobile.isEmpty()){
                var intent =  Intent(this@SplashScreen, MainActivity::class.java)
                intent.putExtra("name",uName)
                intent.putExtra("mobileNumber", uMobile)
                startActivity(intent)
            } else
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
        }else{
            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))

        }

    }
}