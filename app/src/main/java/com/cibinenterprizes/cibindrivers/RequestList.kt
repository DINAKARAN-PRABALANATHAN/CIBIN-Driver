package com.cibinenterprizes.cibindrivers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibinenterprizes.cibindrivers.Model.RequestDetails
import com.cibinenterprizes.cibinenterprises.Model.ComplainDetails
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_request_list.*

class RequestList : AppCompatActivity() {

    private lateinit var recview: RecyclerView
    val idAuth = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_list)

        recview = findViewById(R.id.request_list_recycleview)
        recview.setLayoutManager(LinearLayoutManager(this))

        request_list_back_botton.setOnClickListener {
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        val options: FirebaseRecyclerOptions<RequestDetails> = FirebaseRecyclerOptions.Builder<RequestDetails>().setQuery(
                FirebaseDatabase.getInstance().getReference()
                    .child("User Details").child(idAuth).child("Request Bins"), RequestDetails::class.java).build()

        var adapter: FirebaseRecyclerAdapter<RequestDetails, myviewholder> = object: FirebaseRecyclerAdapter<RequestDetails, myviewholder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {
                var view= LayoutInflater.from(parent.context).inflate(R.layout.request_list_single_view,parent,false)
                return myviewholder(view)
            }

            override fun onBindViewHolder(holder: myviewholder, position: Int, model: RequestDetails) {
                holder.bin1?.setText(model.Bin1)
                holder.bin2?.setText(model.Bin2)
                holder.bin3?.setText(model.Bin3)
                holder.bin4?.setText(model.Bin4)
                holder.bin5?.setText(model.Bin5)
                holder.bin6?.setText(model.Bin6)
                holder.bin7?.setText(model.Bin7)
                holder.bin8?.setText(model.Bin8)
                holder.bin9?.setText(model.Bin9)
                holder.bin10?.setText(model.Bin10)
                holder.bin11?.setText(model.Bin11)
                holder.bin12?.setText(model.Bin12)
                holder.status?.setText(model.Verification)
            }
        }
        recview.setAdapter(adapter)
        adapter.startListening()
    }
    class myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var bin1: TextView? = null
        var bin2: TextView? = null
        var bin3: TextView? = null
        var bin4: TextView? = null
        var bin5: TextView? = null
        var bin6: TextView? = null
        var bin7: TextView? = null
        var bin8: TextView? = null
        var bin9: TextView? = null
        var bin10: TextView? = null
        var bin11: TextView? = null
        var bin12: TextView? = null
        var status: TextView? = null


        init {
            bin1= itemView.findViewById(R.id.work_update_bins_1)
            bin2= itemView.findViewById(R.id.work_update_bins_2)
            bin3= itemView.findViewById(R.id.work_update_bins_3)
            bin4= itemView.findViewById(R.id.work_update_bins_4)
            bin5= itemView.findViewById(R.id.work_update_bins_5)
            bin6= itemView.findViewById(R.id.work_update_bins_6)
            bin7= itemView.findViewById(R.id.work_update_bins_7)
            bin8= itemView.findViewById(R.id.work_update_bins_8)
            bin9= itemView.findViewById(R.id.work_update_bins_9)
            bin10= itemView.findViewById(R.id.work_update_bins_10)
            bin11= itemView.findViewById(R.id.work_update_bins_11)
            bin12= itemView.findViewById(R.id.work_update_bins_12)
            status= itemView.findViewById(R.id.request_list_status)
        }
    }
}