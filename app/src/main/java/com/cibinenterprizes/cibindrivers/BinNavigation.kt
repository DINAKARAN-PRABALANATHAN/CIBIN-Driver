package com.cibinenterprizes.cibindrivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cibinenterprizes.cibindrivers.Model.FCMResponse
import com.cibinenterprizes.cibindrivers.Model.FCMSendData
import com.cibinenterprizes.cibindrivers.Model.WorkReport
import com.cibinenterprizes.cibindrivers.Remote.IFCMService
import com.cibinenterprizes.cibindrivers.Remote.RetrofitFCMClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_bin_navigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class BinNavigation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binIdForUpdate: String
    var database = FirebaseDatabase.getInstance().reference
    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var ifcmService: IFCMService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bin_navigation)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ifcmService = RetrofitFCMClient.getInstance("https://fcm.googleapis.com/").create(IFCMService::class.java)

        binIdForUpdate = getIntent().extras?.get("Bin ID").toString()
        Toast.makeText(this,binIdForUpdate, Toast.LENGTH_SHORT).show()

        bin_navigation_back_botton.setOnClickListener {
            finish()
        }
        bin_navigation_completed_button.setOnClickListener {
            val cal = Calendar.getInstance()

            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            val myLd = LocalDate.of(year, month, day)
            val myLt = LocalTime.of(hour, minute)
            val workReport = WorkReport(binIdForUpdate, "Completed", myLd.toString(), myLt.toString())

            database.child("Work Report").child(myLd.toString()).child(binIdForUpdate).setValue(workReport).addOnCompleteListener {
                Toast.makeText(this,"Completed $binIdForUpdate",Toast.LENGTH_SHORT).show()

            }
            database.child("User Details").child(user?.uid.toString()).child("Work Report").child(myLd.toString()).child(binIdForUpdate).setValue(workReport).addOnCompleteListener {
                database.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var districtName = snapshot.child("User Details").child(user?.uid.toString()).child("Profile").child("district").getValue().toString()
                        var adminToken = snapshot.child("AdminToken").child(districtName).getValue().toString()
                        val dataSend = HashMap<String, String>()
                        dataSend.put("title", "Daily Report")
                        dataSend.put(
                            "content",
                            "Bin $binIdForUpdate is Completed"
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
                                            Toast.makeText(this@BinNavigation, "Failed ", Toast.LENGTH_LONG).show()
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
                finish()
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
            .setContentText("Bin $binIdForUpdate is Completed")
            .setContentTitle("Completion Report")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Bin $binIdForUpdate is Completed").setBigContentTitle("Completion Report").setSummaryText("Successful"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(114, builder.build())
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var districtName = snapshot.child("User Details").child(user?.uid.toString()).child("Profile").child("district").getValue().toString()
                var Lat = snapshot.child("BINS").child(districtName).child(binIdForUpdate).child("lantitude").getValue().toString()
                var Lng = snapshot.child("BINS").child(districtName).child(binIdForUpdate).child("longitude").getValue().toString()

                val sydney = LatLng(Lat.toDouble(), Lng.toDouble())
                mMap.addMarker(MarkerOptions().position(sydney).title("BinID: "+binIdForUpdate))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}