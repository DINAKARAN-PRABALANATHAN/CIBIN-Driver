package com.cibinenterprizes.cibindrivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibinenterprizes.cibindrivers.Model.BinDetails
import com.cibinenterprizes.cibinenterprises.Model.WorkTodoBinDetails
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_work_todo.*

class WorkTodo : AppCompatActivity() {

    private lateinit var recview: RecyclerView
    val idAuth = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val reference = FirebaseDatabase.getInstance().getReference().child("User Details").child(idAuth).child("Work Todo")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_todo)

        recview = findViewById(R.id.bin_list_update_recyclerview)
        recview.setLayoutManager(LinearLayoutManager(this))

        bin_list_update_back_botton.setOnClickListener {
            finish()
        }
        reference.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //createNotification()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun createNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel("2858", "notification", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, "2858")
            .setSmallIcon(R.drawable.logo)
            .setContentText("Work Assigned for you, Please check it out...")
            .setContentTitle("Work Todo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(114, builder.build())
    }
    override fun onStart() {
        super.onStart()
        val options: FirebaseRecyclerOptions<WorkTodoBinDetails> = FirebaseRecyclerOptions.Builder<WorkTodoBinDetails>()
            .setQuery(FirebaseDatabase.getInstance().getReference()
                .child("User Details").child(idAuth).child("Work Todo"), WorkTodoBinDetails::class.java).build()

        val adapter: FirebaseRecyclerAdapter<WorkTodoBinDetails, myviewholder> =object: FirebaseRecyclerAdapter<WorkTodoBinDetails, myviewholder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bin_update, parent, false)
                return myviewholder(view)
            }

            override fun onBindViewHolder(holder: myviewholder, position: Int, model: WorkTodoBinDetails) {
                holder.area?.setText(model.Area_Village)
                holder.binId?.setText(model.BinId.toString())
                holder.locality?.setText(model.Locality)
                holder.city?.setText(model.district)
                holder.loadType?.setText(model.LoadType)
                holder.collectionPeriod?.setText(model.CollectionPeriod)
                holder.mapLantitude?.setText(model.Lantitude)
                holder.mapLongitude?.setText(model.Longitude)
                holder.verificationStatus?.setText(model.Verification)

                holder.itemView.setOnClickListener {
                    val binId = getRef(position).getKey()
                    val intent = Intent(this@WorkTodo,BinNavigation::class.java)
                    intent.putExtra("Bin ID",binId)
                    startActivity(intent)
                }
            }

        }
        recview.setAdapter(adapter)
        adapter.startListening()
    }
    class myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var area: TextView? = null
        var binId: TextView? = null
        var locality: TextView? = null
        var city: TextView? = null
        var loadType: TextView? = null
        var collectionPeriod: TextView? = null
        var mapLantitude: TextView? = null
        var mapLongitude: TextView? = null
        var verificationStatus: TextView? = null

        init {
            area= itemView.findViewById(R.id.bin_list_update_area)
            binId= itemView.findViewById(R.id.bin_list_update_bin_id)
            locality= itemView.findViewById(R.id.bin_list_update_locality)
            city= itemView.findViewById(R.id.bin_list_update_city)
            loadType= itemView.findViewById(R.id.bin_list_update_load_type)
            collectionPeriod= itemView.findViewById(R.id.bin_list_update_collection_period)
            mapLantitude= itemView.findViewById(R.id.bin_list_update_lantitude)
            mapLongitude= itemView.findViewById(R.id.bin_list_update_longitude)
            verificationStatus= itemView.findViewById(R.id.bin_list_update_verification_status)
            super.itemView
        }
    }
}