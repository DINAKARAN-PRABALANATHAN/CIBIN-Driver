package com.cibinenterprizes.cibindrivers

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cibinenterprizes.cibindrivers.Model.FCMResponse
import com.cibinenterprizes.cibindrivers.Model.FCMSendData
import com.cibinenterprizes.cibindrivers.Model.RequestDetails
import com.cibinenterprizes.cibindrivers.Remote.IFCMService
import com.cibinenterprizes.cibindrivers.Remote.RetrofitFCMClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_request_bins.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class RequestBins : AppCompatActivity() {

    val db = FirebaseDatabase.getInstance().reference
    val idAuth = FirebaseAuth.getInstance().currentUser?.uid.toString()
    lateinit var ifcmService: IFCMService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_bins)

        ifcmService = RetrofitFCMClient.getInstance("https://fcm.googleapis.com/").create(IFCMService::class.java)

        request_bins_back_botton.setOnClickListener {
            finish()
        }
        var databaseReference = FirebaseDatabase.getInstance().getReference()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var requestCount = snapshot.child("User Details").child("Request ID").getValue().toString()
                val requestId = requestCount.toInt()
                var id = requestId + 1
                request_bins_submit_botton.setOnClickListener {
                    var driverMobile = snapshot.child("User Details").child(idAuth).child("Profile")
                        .child("mobile").getValue().toString()
                    var driverName = snapshot.child("User Details").child(idAuth).child("Profile")
                        .child("username").getValue().toString()
                    val verification = "Pending"
                    var requestDetails = RequestDetails(
                        request_bins_1.text.trim().toString(),
                        request_bins_2.text.trim().toString(),
                        request_bins_3.text.trim().toString(),
                        request_bins_4.text.trim().toString(),
                        request_bins_5.text.trim().toString(),
                        request_bins_6.text.trim().toString(),
                        request_bins_7.text.trim().toString(),
                        request_bins_8.text.trim().toString(),
                        request_bins_9.text.trim().toString(),
                        request_bins_10.text.trim().toString(),
                        request_bins_11.text.trim().toString(),
                        request_bins_12.text.trim().toString(),
                        idAuth,
                        driverMobile,
                        driverName,
                        verification
                    )
                    val spinner = Spinner(this@RequestBins)
                    val districts = arrayOf("Choose a district", "Ariyalur", "Chengalpattu", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kallakurichi", "Kanchipuram", "Kanyakumari", "Karur", "Krishnagiri", "Madurai", "Mayiladuthurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur", "Pudukkottai", "Ramanathapuram", "Ranipet", "Salem", "Sivagangai", "Tenkasi", "Thanjavur", "Theni", "Thoothukudi", "Tiruchirappalli", "Tirunelveli", "Tirupattur", "Tiruppur","Tiruvallur", "Tiruvannamalai", "Tiruvarur", "Vellore", "Viluppuram", "Virudhunagar")
                    val arrayAdapter = ArrayAdapter(this@RequestBins, android.R.layout.simple_spinner_item, districts)
                    spinner.adapter = arrayAdapter
                    val alertDialogBuilder = AlertDialog.Builder(this@RequestBins)
                    alertDialogBuilder.setTitle("Choose a district")
                    alertDialogBuilder.setView(spinner)
                    alertDialogBuilder.setPositiveButton("Ok", null)
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        val district = spinner.selectedItem.toString()
                        var adminToken = snapshot.child("AdminToken").child(district).getValue().toString()
                        alertDialog.dismiss()
                        if (district == "Choose a district"){
                            Toast.makeText(this@RequestBins, "Please choose a district",Toast.LENGTH_LONG).show()
                        }else{
                            db.child("Request Bins").child(district).child(id.toString()).setValue(requestDetails)
                                .addOnCompleteListener {
                                    db.child("User Details").child("Request ID").setValue(id.toString())
                                        .addOnCompleteListener {
                                            db.child("User Details").child(idAuth).child("Request Bins")
                                                .child(id.toString()).setValue(requestDetails)
                                                .addOnCompleteListener {
                                                    val dataSend = HashMap<String, String>()
                                                    dataSend.put("title", "New Bin Request")
                                                    dataSend.put(
                                                        "content",
                                                        "New Bin request arrived, check it and update the status"
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
                                                                        Toast.makeText(this@RequestBins, "Failed ", Toast.LENGTH_LONG).show()
                                                                    }
                                                                }
                                                            }

                                                            override fun onFailure(call: Call<FCMResponse?>, t: Throwable?) {

                                                            }
                                                        })

                                                    Toast.makeText(this@RequestBins, "Request Completed", Toast.LENGTH_SHORT).show()
                                                    finish()
                                                }
                                        }
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}