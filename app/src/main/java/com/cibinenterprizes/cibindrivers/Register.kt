package com.cibinenterprizes.cibindrivers

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cibinenterprizes.cibindrivers.Model.FCMResponse
import com.cibinenterprizes.cibindrivers.Model.FCMSendData
import com.cibinenterprizes.cibindrivers.Remote.IFCMService
import com.cibinenterprizes.cibindrivers.Remote.RetrofitFCMClient
import com.cibinenterprizes.cibinenterprises.Model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.HashMap

class Register : AppCompatActivity() {
    lateinit var handler: Handler
    private lateinit var auth: FirebaseAuth
    var TAKE_IMAGE_CODE = 10001
    var id: Int = 0
    lateinit var progressDialog: Dialog
    lateinit var ifcmService: IFCMService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ifcmService = RetrofitFCMClient.getInstance("https://fcm.googleapis.com/").create(IFCMService::class.java)

        auth = FirebaseAuth.getInstance()
        val profilePhoto = findViewById<CircleImageView>(R.id.registration_profile)

        registration_login.setOnClickListener{
            startActivity(Intent(this,Login::class.java))
        }
        registration_profile.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, TAKE_IMAGE_CODE)
            }
        }
        register_terms_and_conditions_view.setOnClickListener {
            startActivity(Intent(this,TermsAndConditions::class.java))
        }
        registration_button.setOnClickListener {
            profilePhoto.setImageResource(R.drawable.waring)
            return@setOnClickListener
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE){
            when(resultCode){
                RESULT_OK -> bitMap(data)
            }
        }
    }

    private fun bitMap(data: Intent?) {
        val bitmap = data?.getExtras()?.get("data")
        registration_profile.setImageBitmap(bitmap as Bitmap?)
        registration_button.setOnClickListener {
            if(registration_email_id.text.trim().toString().isNotEmpty() || registration_password.text.trim().toString().isNotEmpty() || registration_user_name.text.trim().toString().isNotEmpty() || registration_mobile_number.text.trim().toString().isNotEmpty()){
                if(registration_password.text.trim().toString() == registration_conform_password.text.trim().toString()){
                    if (register_terms_and_conditions.isChecked){
                        progressDialog = ProgressDialog(this)

                        val spinner = Spinner(this)
                        val districts = arrayOf("Choose a district", "Ariyalur", "Chengalpattu", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kallakurichi", "Kanchipuram", "Kanyakumari", "Karur", "Krishnagiri", "Madurai", "Mayiladuthurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur", "Pudukkottai", "Ramanathapuram", "Ranipet", "Salem", "Sivagangai", "Tenkasi", "Thanjavur", "Theni", "Thoothukudi", "Tiruchirappalli", "Tirunelveli", "Tirupattur", "Tiruppur","Tiruvallur", "Tiruvannamalai", "Tiruvarur", "Vellore", "Viluppuram", "Virudhunagar")
                        val arrayAdapter = ArrayAdapter(this@Register, android.R.layout.simple_spinner_item, districts)
                        spinner.adapter = arrayAdapter
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setTitle("Choose a district")
                        alertDialogBuilder.setView(spinner)
                        alertDialogBuilder.setPositiveButton("Ok", null)
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                            val district = spinner.selectedItem.toString()
                            alertDialog.dismiss()
                            if (district == "Choose a district"){
                                Toast.makeText(this, "Please choose a district",Toast.LENGTH_LONG).show()
                            }else{
                                progressDialog.show()
                                progressDialog.setContentView(R.layout.full_screen_progress_bar)
                                progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                progressDialog.setCancelable(false)

                                auth.createUserWithEmailAndPassword(registration_email_id.text.trim().toString(),registration_password.text.trim().toString()).addOnCompleteListener(this) { task ->
                                    if(task.isSuccessful){

                                        val user = auth.currentUser
                                        user?.sendEmailVerification()?.addOnCompleteListener {
                                            if (it.isSuccessful){
                                                Toast.makeText(this, "Registration Successful\nVerification mail send to your mail",Toast.LENGTH_LONG).show()
                                                handleUpload(bitmap, district)
                                            }
                                        }
                                    }else{
                                        Toast.makeText(this, "User Creation is Failed...",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                        }
                    }else{
                        Toast.makeText(this, "Please accept the Terms and conditions",Toast.LENGTH_LONG).show()
                    }
                }else{
                    registration_conform_password.error = "Password need to be same"
                    registration_conform_password.requestFocus()
                    return@setOnClickListener
                }
            }else{
                Toast.makeText(this, "Input Reqired", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleUpload(bitmap: Bitmap?, district: String) {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100, baos)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseStorage = FirebaseStorage.getInstance().reference.child(uid+".jpeg")
        firebaseStorage.putBytes(baos.toByteArray()).addOnSuccessListener {
            getDownloadUrl(firebaseStorage, district)
        }.addOnFailureListener {
            Toast.makeText(this, "Firebase Storage is not done", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDownloadUrl(firebaseStorage: StorageReference, district: String) {
        firebaseStorage.downloadUrl.addOnSuccessListener {

            setUserProfileUrl(it, district)
        }
    }

    private fun setUserProfileUrl(it: Uri?, district: String) {
        val url = it.toString().substring(0,it.toString().indexOf("&token"))
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance().reference
        val request = UserProfileChangeRequest.Builder().setPhotoUri(it).build()
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val driverCount = snapshot.child("User Details").child("Driver").getValue().toString()
                val driverId = driverCount.toInt()
                id = driverId + 1
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        handler = Handler()
        handler.postDelayed({
            user?.updateProfile(request)?.addOnSuccessListener {
                val idAuth = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val userProfile = UserProfile(registration_user_name.text.trim().toString(), registration_email_id.text.trim().toString(), registration_mobile_number.text.trim().toString(), url, "CD$id", district)
                db.child("User Details").child(idAuth).child("Profile").setValue(userProfile).addOnCompleteListener {
                    db.child("Drivers").child("CD$id").setValue(userProfile).addOnCompleteListener {
                        db.child("User Details").child("Driver").setValue(id).addOnCompleteListener {
                            db.child("User Details").child(idAuth).child("Profile").child("TermsAndConditions").setValue("Accepted").addOnCompleteListener{
                                progressDialog.dismiss()
                                Toast.makeText(this@Register, "Update Successfully", Toast.LENGTH_SHORT).show()
                                db.addValueEventListener(object: ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var adminToken = snapshot.child("AdminToken").getValue().toString()
                                        val dataSend = HashMap<String, String>()
                                        dataSend.put("title", "New Driver")
                                        dataSend.put(
                                            "content",
                                            "New driver registered. Verify the user and alart the bins."
                                        )
                                        val sendData = FCMSendData(
                                            adminToken,
                                            dataSend
                                        )
                                        ifcmService.sendNotification(sendData)
                                            .enqueue(object : Callback<FCMResponse?> {

                                                override fun onResponse(call: Call<FCMResponse?>, response: Response<FCMResponse?>) {
                                                    if (response.code() == 200) {
                                                        if (response.body()!!.success != 1) {
                                                            Toast.makeText(this@Register, "Failed ", Toast.LENGTH_LONG).show()
                                                        }
                                                    }
                                                }

                                                override fun onFailure(call: Call<FCMResponse?>, t: Throwable?) {

                                                }
                                            })
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                })
                                createNotification()
                                startActivity(Intent(this@Register, Login::class.java))
                            }
                        }
                    }
                }
            }?.addOnFailureListener {
                Toast.makeText(this@Register, "Profile image failed", Toast.LENGTH_SHORT).show()
            }
        }, 5000)
    }

    private fun createNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel("2858", "notification", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, "2858")
            .setSmallIcon(R.drawable.logo)
            .setContentText("Registered successfully, Please complete verification first")
            .setContentTitle("Registered Successfully")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Registered successfully, Please complete verification first").setBigContentTitle("Registered Successfully").setSummaryText("Successful"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(114, builder.build())
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser

        if(user != null){
            if(auth.currentUser?.isEmailVerified!!){
                startActivity(Intent(this, HomeActivitty::class.java))
                finish()
            }else{
                Toast.makeText(this, "Email address is not verified. Please Verify your mail ID", Toast.LENGTH_LONG).show()
            }
        }
    }
}