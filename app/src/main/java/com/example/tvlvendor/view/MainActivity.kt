package com.rideyrbike1.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rideyrbike1.app.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPartsButton = findViewById<View>(R.id.ViewPartsButton)
        val viewAppointmentsButton = findViewById<View>(R.id.ViewAppointmentsButton)
        val approveAppointmentsButton = findViewById<View>(R.id.ApproveAppointmentsButton)
        val updateLocationButton = findViewById<View>(R.id.UpdateLocationButton)
        val addPartButton =findViewById<View>(R.id.AddPartsButton)
        val updatePartButton=findViewById<View>(R.id.UpdatePartsButton)

        viewPartsButton.setOnClickListener {
            val value  = Intent(this, ViewPartsActivity::class.java)
            startActivity(value)
        }
        viewAppointmentsButton.setOnClickListener {
            val value  = Intent(this, ViewAppointmentsActivity::class.java)
            startActivity(value)
        }
        approveAppointmentsButton.setOnClickListener {
            val value  = Intent(this, ApproveAppointmentsActivity::class.java)
            startActivity(value)
        }
        updateLocationButton.setOnClickListener {
            val value  = Intent(this, UpdateMapsActivity::class.java)
            startActivity(value)
        }
        addPartButton.setOnClickListener {
            startActivity(Intent(this,AddPartActivity::class.java))
        }
        updatePartButton.setOnClickListener {
            startActivity(Intent(this,UpdatePartActivity::class.java))
        }


    }
}