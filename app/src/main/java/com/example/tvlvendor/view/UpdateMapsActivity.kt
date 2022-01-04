package com.rideyrbike1.app.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.rideyrbike1.app.R
import com.rideyrbike1.app.viewmodel.UpdateMapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker

class UpdateMapsActivity : AppCompatActivity() {

    private var locationPermissionGranted = false
    private var lastKnownLocation: LatLng? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var updateMapsViewModel: UpdateMapsViewModel = UpdateMapsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_maps)

        fusedLocationProviderClient = FusedLocationProviderClient(this)

        val addressField = findViewById<EditText>(R.id.AddressField)
        val addressButton = findViewById<Button>(R.id.UpdateAddressButton)
        val updateButton = findViewById<Button>(R.id.UpdateButton)

        addressField.isEnabled = false
        addressButton.isEnabled = false
        updateButton.isEnabled = false

        updateMapsViewModel.data.observe(this, Observer {
            addressField.setText(it.address)
            lastKnownLocation = it.location

            addressButton.isEnabled = true
            updateButton.isEnabled = true
        })

        updateButton.setOnClickListener{
            getLocationPermission()
            if(locationPermissionGranted){
                updateLocation()
            }
        }

        addressButton.setOnClickListener{
            addressField.isEnabled = !addressField.isEnabled
            if(addressField.isEnabled){
                addressButton.text = "Update Address"
            }
            else{
                addressButton.text = "Edit Address"
                updateMapsViewModel.updateAddress(addressField.text.toString())
            }
        }

        updateMapsViewModel.loadData()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        val MAP_BUTTON_REQUEST_CODE = 1
        val key = resources.getString(R.string.google_maps_key)
        val intentBuilder = PlacePicker.IntentBuilder()
                .setLatLong(40.748672, -73.985628)  // Initial Latitude and Longitude the Map will load into
                .showLatLong(true)  // Show Coordinates in the Activity
                .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                .setAddressRequired(false) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
//                        .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
//                        .setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
//                        .setMarkerImageImageColor(R.color.colorPrimary)
//                        .setFabColor(R.color.fabColor)
//                        .setPrimaryTextColor(R.color.primaryTextColor) // Change text color of Shortened Address
//                        .setSecondaryTextColor(R.color.secondaryTextColor) // Change text color of full Address
//                        .setBottomViewColor(R.color.bottomViewColor) // Change Address View Background Color (Default: White)
//                        .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
//                        .setMapType(MapType.NORMAL)
                .setPlaceSearchBar(true, key) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
//                        .onlyCoordinates(true)  //Get only Coordinates from Place Picke   r
                .hideLocationButton(true)   //Hide Location Button (Default: false)
//                        .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false
        if(lastKnownLocation != null){
            intentBuilder.setLatLong(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            val intent = intentBuilder.build(this)
            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
        }

        else if(locationPermissionGranted){
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                lastKnownLocation = LatLng(it.latitude, it.longitude)
                intentBuilder.setLatLong(it.latitude, it.longitude)
                val intent = intentBuilder.build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }.addOnFailureListener{
                val intent = intentBuilder.build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
            }
        }else{
            val intent = intentBuilder.build(this)
            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
        }

    }

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("MAP", "getLocationPermission")
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MAP", "onRequestPermissionResult")
                    locationPermissionGranted = true
                }
            }
        }
        updateLocation()
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                Log.d("MAP", addressData.toString())
                val builder: AlertDialog.Builder = this.let {
                    AlertDialog.Builder(it)
                }

                builder.setMessage("Update Address Automatically?")
                        .setTitle("Update Address")
                builder.apply {
                    setPositiveButton("Yes") { _, _ ->
                        if (addressData != null) {
                            val address = addressData.addressList?.get(0)?.getAddressLine(0)
                            updateMapsViewModel.updateLocation(LatLng(addressData.latitude,
                                    addressData.longitude), address)
                        }
                    }
                    setNegativeButton("No") { _, _ ->
                        if (addressData != null) {
                            updateMapsViewModel.updateLocation(LatLng(addressData.latitude, addressData.longitude), null)
                        }
                    }
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}