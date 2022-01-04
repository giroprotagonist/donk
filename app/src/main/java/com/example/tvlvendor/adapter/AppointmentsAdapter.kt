package com.rideyrbike1.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rideyrbike1.app.R
import com.rideyrbike1.app.model.Appointment
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

class AppointmentsAdapter (var dataSet: List<Appointment>) :
    RecyclerView.Adapter<AppointmentsAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.label_name)
        val phone: TextView = view.findViewById(R.id.label_phone)
        val time: TextView = view.findViewById(R.id.label_time)
        val license: TextView = view.findViewById(R.id.label_license)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.appointment_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val appointment = dataSet[position]
        viewHolder.name.text = appointment.owner_name
        viewHolder.phone.text = appointment.phone
        viewHolder.time.text = formatDate(appointment.time)
        viewHolder.license.text = appointment.vehicle?.license ?: ""

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    @SuppressLint("SimpleDateFormat")
    fun formatDate(time: Timestamp?): String? {

        if(time == null) return ""
        return SimpleDateFormat("HH:mm dd-MMMM-yyyy").format(time.toDate())
    }

}