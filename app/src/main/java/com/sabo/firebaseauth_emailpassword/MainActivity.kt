package com.rideyrbike0.app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.rideyrbike0.app.Helpers.Constants
import com.rideyrbike0.app.databinding.ActivityMainBinding

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.tvUID.text = "UID : ${auth.currentUser?.uid}"

        val collection = Firebase.firestore.collection(Constants.USER_COLLECTION)
        val userRef = Firebase.database.getReference(Constants.USER_REFERENCE)


        binding.spDatabase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val value = parent!!.getItemAtPosition(pos).toString()
//                val pos = pos
//                binding.tvResult.text = pos.toString()

                val sweetLoading = SweetAlertDialog(this@MainActivity, SweetAlertDialog.PROGRESS_TYPE)
                sweetLoading.progressHelper.barColor = resources.getColor(R.color.amber_600, theme)
                sweetLoading.titleText = "Please wait..."
                sweetLoading.setCancelable(false)
                sweetLoading.show()

                auth.currentUser?.let {
                    when(pos){
                        0 -> {
                            userRef.child(it.uid)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val value = snapshot.value
                                        binding.tvResult.text = "Realtime Database : $value"
                                        sweetLoading.dismissWithAnimation()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        binding.tvResult.text = "Realtime Database : ${error.message}"
                                        sweetLoading.dismissWithAnimation()
                                    }
                                })
                        }
                        1 -> {
                            collection.document(it.uid)
                                .get()
                                .addOnSuccessListener { documents ->
                                    val value = documents.data
                                    binding.tvResult.text = "Cloud Firestore : $value"
                                    sweetLoading.dismissWithAnimation()
                                }
                                .addOnFailureListener { err ->
                                    binding.tvResult.text = "Cloud Firestore : ${err.message}"
                                    sweetLoading.dismissWithAnimation()
                                }
                        }
                        else -> return@let
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}