package com.rideyrbike1.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rideyrbike1.app.model.Appointment
import com.rideyrbike1.app.model.UserVehicle
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentsViewModel {
    private lateinit var auth: FirebaseAuth
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var data = MutableLiveData<List<Appointment>>()

    fun loadAppointments(approved: Boolean){
        auth = FirebaseAuth.getInstance()
        db.collection("Appointment").whereEqualTo("vendor_id", auth.currentUser!!.uid)
                .whereEqualTo("approved", approved).get().addOnSuccessListener { res ->

            val appointments = mutableListOf<Appointment>()
            val owner_ids = mutableListOf<String>()
            val vehicle_ids = mutableListOf<String>()

            for (doc in res){
                owner_ids.add(doc["owner_id"] as String)
                vehicle_ids.add(doc["uservehicle_id"] as String)
                appointments.add(Appointment(id=doc.id, time=doc["time"] as Timestamp?, owner_id = doc["owner_id"] as String?,
                vendor_id = auth.currentUser!!.uid))
            }
            if(owner_ids.size != 0) {
                db.collection("Owner").whereIn("uid", owner_ids).get().addOnSuccessListener { querySnapshot ->

                    for (doc in querySnapshot) {
                        for(i in 0 until owner_ids.size){
                            if(owner_ids[i] == doc.id) {
                                appointments[i].owner_name = doc["name"] as String?
                                appointments[i].phone = doc["phone"] as String?
                            }
                        }
                    }
                    val idRef = com.google.firebase.firestore.FieldPath.documentId()
                    db.collection("UserVehicle").whereIn(idRef, vehicle_ids).get().addOnSuccessListener { vehicleSnapshot ->

                        for (doc in vehicleSnapshot){
                            for(i in 0 until vehicle_ids.size){
                                if(vehicle_ids[i] == doc.id) {
                                    appointments[i].vehicle = UserVehicle(id=doc.id, license=doc["lisenceNumber"] as String?)
                                }
                            }
                        }

                        appointments.sortBy{it.time}
                        data.value = appointments
                    }.addOnFailureListener{
                        appointments.sortBy{it.time}
                        data.value = appointments
                    }
                }.addOnFailureListener{
                    data.value = appointments
                }
            }

        }.addOnFailureListener{
            Log.d("ERROR", it.toString())
            data.value = null
        }
    }

    fun approve(appointment: Appointment) {
        if(appointment.id != null)
            db.collection("Appointment").document(appointment.id).update("approved", true)
    }

    fun remove(appointment: Appointment) {
        if(appointment.id != null)
            db.collection("Appointment").document(appointment.id).delete()
    }
}