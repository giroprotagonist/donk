package com.rideyrbike1.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rideyrbike1.app.R
import com.rideyrbike1.app.adapter.AppointmentsAdapter
import com.rideyrbike1.app.viewmodel.AppointmentsViewModel

class ViewAppointmentsActivity : AppCompatActivity() {
    private var appointmentsViewModel: AppointmentsViewModel = AppointmentsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_appointments)

        val appointmentsRecyclerView = findViewById<RecyclerView>(R.id.recyclerview_appointments)
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        val appointmentsAdapter = AppointmentsAdapter(emptyList())

        appointmentsRecyclerView.adapter = appointmentsAdapter

        appointmentsViewModel.data.observe(this, Observer {
            Log.d("APPOINTMENT", "UPDATING")
            appointmentsAdapter.dataSet = it
            appointmentsAdapter.notifyDataSetChanged()
        })



        appointmentsViewModel.loadAppointments(true)
    }
}