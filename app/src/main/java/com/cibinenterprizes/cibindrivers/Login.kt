package com.cibinenterprizes.cibindrivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        login_register.setOnClickListener{
            startActivity(Intent(this, Register::class.java))
            finish()
        }
        login_button.setOnClickListener {
            if(login_emailid.text.trim().toString().isNotEmpty() || login_password.text.trim().toString().isNotEmpty()){

                signInUser(login_emailid.text.trim().toString(), login_password.text.trim().toString())

            }else{
                Toast.makeText(this, "Input Required", Toast.LENGTH_LONG).show()
            }
        }
        login_forgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
        }

    }
    private fun signInUser(email:String, password:String){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    if(auth.currentUser?.isEmailVerified!!){
                        //NetworkTask(this).execute()
                        startActivity(Intent(this, HomeActivitty::class.java))
                        createNotification()
                        finish()
                    }else{
                        Toast.makeText(this, "Email address is not verified. Please Verify your mail ID", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this, "Email Id or Password is not Correct !! "+task.exception, Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("2858", "notification", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, "2858")
            .setSmallIcon(R.drawable.logo)
            .setContentText("Welcome to CIBIN Driver family. All the best.")
            .setContentTitle("Login Successful")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Welcome to CIBIN Driver family. All the best.").setBigContentTitle("Login Successful").setSummaryText("Successful"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(114, builder.build())
    }
}