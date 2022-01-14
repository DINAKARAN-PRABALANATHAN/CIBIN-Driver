package com.cibinenterprizes.cibindrivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cibinenterprizes.cibindrivers.Model.FCMResponse
import com.cibinenterprizes.cibindrivers.Model.FCMSendData
import com.cibinenterprizes.cibindrivers.Remote.IFCMService
import com.cibinenterprizes.cibindrivers.Remote.RetrofitFCMClient
import com.cibinenterprizes.cibinenterprises.Model.BinExtra
import com.cibinenterprizes.cibinenterprises.Model.ComplainDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_register_complaints.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class RegisterComplaints : AppCompatActivity() {

    val idAuth = FirebaseAuth.getInstance().currentUser
    val databaseReference= FirebaseDatabase.getInstance().reference
    lateinit var ifcmService: IFCMService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_complaints)
        register_complaints_email.setText(idAuth?.email)

        ifcmService = RetrofitFCMClient.getInstance("https://fcm.googleapis.com/").create(IFCMService::class.java)

        register_complaints_back_botton.setOnClickListener {
            finish()
        }
        var getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val complaintCount = snapshot.child("User Details").child("COMPLAINT ID").getValue().toString()
                val complaintId = complaintCount.toInt()
                var id = complaintId + 1
                register_complaints_botton.setOnClickListener{
                    var districtName = snapshot.child("User Details").child(idAuth?.uid.toString()).child("Profile").child("district").getValue().toString()
                    val area = register_complaints_area.text.toString()
                    val binId = register_complaints_bin_id.text.toString()
                    val complaint = register_complaints_complaints.text.toString()
                    val status = "Pending"
                    val complaintDetail = ComplainDetails(idAuth?.email,binId,area,complaint,status,id.toString())
                    val userName = snapshot.child("User Details").child(districtName).child(idAuth?.uid.toString()).child("Profile").child("username").getValue()
                    val emailId = snapshot.child("User Details").child(districtName).child(idAuth?.uid.toString()).child("Profile").child("emailId").getValue()
                    val mobile = snapshot.child("User Details").child(districtName).child(idAuth?.uid.toString()).child("Profile").child("mobile").getValue()
                    val binExtra = BinExtra(userName as String?, emailId as String?, mobile as String?,idAuth?.uid.toString())
                    databaseReference.child("User Details").child(idAuth?.uid.toString()).child("Complaints").child(id.toString()).setValue(complaintDetail).addOnCompleteListener {
                        databaseReference.child("User Details").child("COMPLAINT ID").setValue(id).addOnCompleteListener{
                            databaseReference.child("Complaints").child(districtName).child(id.toString()).setValue(complaintDetail).addOnCompleteListener {
                                databaseReference.child("Complaints").child(districtName).child(id.toString()).child("UserDetails").setValue(binExtra).addOnCompleteListener {
                                    var adminToken = snapshot.child("AdminToken").child(districtName).getValue().toString()
                                    val dataSend = HashMap<String, String>()
                                    dataSend.put("title", "New Complaint")
                                    dataSend.put(
                                        "content",
                                        "New Complaint Registered. verify it soon"
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
                                                        Toast.makeText(this@RegisterComplaints, "Failed ", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }

                                            override fun onFailure(call: Call<FCMResponse?>, t: Throwable?) {

                                            }
                                        })
                                    createNotification()
                                    //Toast.makeText(this@RegisterComplaints,"Complain Registered successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        databaseReference.addValueEventListener(getData)
        databaseReference.addListenerForSingleValueEvent(getData)
    }
    private fun createNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel("2858", "notification", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, "2858")
            .setSmallIcon(R.drawable.logo)
            .setContentText("Registered successfully, Please wait for verification")
            .setContentTitle("Registered Successfully")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Registered successfully, Please wait for verification").setBigContentTitle("Registered Successfully").setSummaryText("Successful"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(114, builder.build())
    }
}