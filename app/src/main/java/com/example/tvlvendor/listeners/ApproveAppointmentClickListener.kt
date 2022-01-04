package com.rideyrbike1.app.listeners

import android.view.View
import com.rideyrbike1.app.model.Appointment

interface ApproveAppointmentClickListener {
    fun onItemClick(view: View, appointment: Appointment, position: Int, isApproved: Boolean)
}