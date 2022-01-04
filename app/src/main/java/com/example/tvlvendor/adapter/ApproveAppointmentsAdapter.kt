package com.rideyrbike1.app.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rideyrbike1.app.R
import com.rideyrbike1.app.listeners.ApproveAppointmentClickListener
import com.rideyrbike1.app.listeners.PartClickListener
import com.rideyrbike1.app.model.Appointment
import com.rideyrbike1.app.model.Part
import com.google.firebase.Timestamp
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class ApproveAppointmentsAdapter (var dataSet: MutableList<Appointment>, private val itemClickListener: ApproveAppointmentClickListener) :
    RecyclerView.Adapter<ApproveAppointmentsAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.label_name)
        val phone: TextView = view.findViewById(R.id.label_phone)
        val time: TextView = view.findViewById(R.id.label_time)
        val license: TextView = view.findViewById(R.id.label_license)

        val approveButton: Button = view.findViewById(R.id.button_approve)
        val rejectButton: Button = view.findViewById(R.id.button_reject)
        var _view: View = view

        fun bind(appointment: Appointment, position: Int, clickListener: ApproveAppointmentClickListener)
        {
            approveButton.setOnClickListener {
                clickListener.onItemClick(_view, appointment, position, true)
            }
            rejectButton.setOnClickListener {
                clickListener.onItemClick(_view, appointment, position, false)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.approve_appointment_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val appointment = dataSet[position]

        viewHolder.name.text = appointment.owner_name
        viewHolder.phone.text = appointment.phone
        viewHolder.time.text = formatDate(appointment.time)
        viewHolder.license.text = appointment.vehicle?.license ?: ""

        viewHolder.bind(dataSet[position], position, itemClickListener)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    @SuppressLint("SimpleDateFormat")
    fun formatDate(time: Timestamp?): String? {

        if(time == null) return ""
        return SimpleDateFormat("HH:mm dd-MMMM-yyyy").format(time.toDate())
    }

    fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyDataSetChanged()
    }

}