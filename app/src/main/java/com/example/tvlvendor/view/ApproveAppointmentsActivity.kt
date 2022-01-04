package com.rideyrbike1.app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rideyrbike1.app.R
import com.rideyrbike1.app.adapter.ApproveAppointmentsAdapter
import com.rideyrbike1.app.listeners.ApproveAppointmentClickListener
import com.rideyrbike1.app.model.Appointment
import com.rideyrbike1.app.viewmodel.AppointmentsViewModel


class ApproveAppointmentsActivity : AppCompatActivity(), ApproveAppointmentClickListener {
    private var appointmentsViewModel: AppointmentsViewModel = AppointmentsViewModel()
    private lateinit var appointmentsAdapter: ApproveAppointmentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approve_appointments)

        val appointmentsRecyclerView = findViewById<RecyclerView>(R.id.recyclerview_appointments)
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        appointmentsAdapter = ApproveAppointmentsAdapter(mutableListOf(), this)

        appointmentsRecyclerView.adapter = appointmentsAdapter

        appointmentsViewModel.data.observe(this, Observer {
            Log.d("APPOINTMENT", "UPDATING")
            appointmentsAdapter.dataSet = it.toMutableList()
            appointmentsAdapter.notifyDataSetChanged()
        })

        appointmentsViewModel.loadAppointments(false)
    }

    override fun onItemClick(view: View, appointment: Appointment, position: Int, isApproved: Boolean) {
        if(isApproved){
            appointmentsViewModel.approve(appointment)
        }
        else{
            appointmentsViewModel.remove(appointment)
        }
        val anim: Animation = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right)
        anim.duration = 300
        view.startAnimation(anim)

        Handler(Looper.getMainLooper()).postDelayed({

            appointmentsAdapter.removeItem(position)
        }, anim.duration)
    }

}