package com.rideyrbike1.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rideyrbike1.app.model.Vendor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions


class UpdateMapsViewModel : ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var data = MutableLiveData<Vendor>()

    fun loadData(){
        auth = FirebaseAuth.getInstance()
        db.collection("Vendor").document(auth.currentUser?.uid!!).get().addOnSuccessListener { res ->

            val address = res["address"]
            val geoPoint = res.getGeoPoint("location")

            val location = if (geoPoint != null) LatLng(geoPoint.latitude, geoPoint.longitude) else null
            data.value = Vendor(id = auth.currentUser!!.uid, address = address as String?, location = location)
        }.addOnFailureListener{
            Log.d("ERROR", it.toString())
            data.value = null
        }
    }

    fun updateLocation(latLng: LatLng, address: String?) {
        if (address != null)
            updateAddress(address)
        val updateData = hashMapOf("location" to GeoPoint(latLng.latitude, latLng.longitude))
        db.collection("Vendor").document(auth.currentUser?.uid!!).set(updateData, SetOptions.merge())
        val tempData = data.value
        if (tempData != null) {
            if (address != null)
                tempData.address = address
            tempData.location = latLng
        }
        data.value = tempData
    }

    fun updateAddress(address: String){

        data.value?.address = address
        val updateData = hashMapOf("address" to address)
        db.collection("Vendor").document(auth.currentUser?.uid!!).set(updateData, SetOptions.merge())
    }
}