package com.rideyrbike1.app.viewmodel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.rideyrbike1.app.model.Result


class SignUpViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private val _result = MutableLiveData<Result<String>>()
    val result: LiveData<Result<String>> = _result

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    /**
     * if sign up is success add the record into
     * owner table.
     */
    public fun createAccount(
        email: String, password: String, name: String,
        cnic: String, phone: String, _activity: Activity
    ) {

        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(_activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    if(auth.currentUser != null) {
                        val data = hashMapOf(
                            "uid" to auth.currentUser!!.uid,
                            "name" to name,
                            "email" to email,
                            "cnic" to cnic,
                            "phone" to phone
                        )
                        db.collection("Vendor").document(auth.currentUser!!.uid).set(data)
                            .addOnSuccessListener { documentReference ->
                                _result.value = Result.Success("Success")
                                Log.d("Result", "Success")
                            }.addOnFailureListener { e ->
                                _result.value = Result.Error(e)
                                Log.d("Result", e.toString())
                            }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(_activity, "Error Signing Up", Toast.LENGTH_LONG).show()
                }
            }



    }


}
