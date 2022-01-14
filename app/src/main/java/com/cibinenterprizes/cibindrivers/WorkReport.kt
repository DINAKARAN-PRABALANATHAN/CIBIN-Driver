package com.cibinenterprizes.cibindrivers

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibinenterprizes.cibinadmin.Model.WorkReportModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_work_report.*
import java.time.LocalDate
import java.util.*

class WorkReport : AppCompatActivity() {

    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var recview: RecyclerView
    lateinit var date: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_report)

        val cal = Calendar.getInstance()

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val myLd = LocalDate.of(year, month, day)
        date = myLd.toString()
        work_report_searchView.setText(myLd.toString())

        work_report_search.setOnClickListener {
            date = work_report_searchView.text.trim().toString()
            onStart()
        }

        recview = findViewById(R.id.work_report_recyclerview)
        recview.setLayoutManager(LinearLayoutManager(this))

        work_report_back_botton.setOnClickListener {
            finish()
        }
    }
    override fun onStart() {
        super.onStart()

        val options: FirebaseRecyclerOptions<WorkReportModel> = FirebaseRecyclerOptions.Builder<WorkReportModel>()
            .setQuery(FirebaseDatabase.getInstance().getReference().child("User Details").child(user?.uid.toString()).child("Work Report").child(date), WorkReportModel::class.java).build()

        var adapter: FirebaseRecyclerAdapter<WorkReportModel, myviewholder> =object: FirebaseRecyclerAdapter<WorkReportModel, myviewholder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {
                var view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_report_single_view, parent, false)
                return myviewholder(view)
            }

            override fun onBindViewHolder(holder: myviewholder, position: Int, model: WorkReportModel) {

                holder.binId?.setText(model.binId)
                holder.status?.setText(model.completionStatus)
                holder.date?.setText(model.date)
                holder.time?.setText(model.time)
            }

        }
        recview.setAdapter(adapter)
        adapter.startListening()
    }
    class myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var binId: TextView? = null
        var status: TextView? = null
        var date: TextView? = null
        var time: TextView? = null

        init {

            binId= itemView.findViewById(R.id.work_report_binId)
            status= itemView.findViewById(R.id.work_report_status)
            date= itemView.findViewById(R.id.work_report_date)
            time= itemView.findViewById(R.id.work_report_time)
            super.itemView
        }
    }
}