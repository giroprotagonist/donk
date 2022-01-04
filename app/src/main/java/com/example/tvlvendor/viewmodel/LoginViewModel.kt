package com.rideyrbike1.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var data = MutableLiveData<FirebaseUser>()


    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    data.postValue(auth.currentUser)
                    if(auth.currentUser != null) {
                        db.collection(auth.currentUser!!.uid).get().addOnSuccessListener {
                            data.value = auth.currentUser
                        }.addOnFailureListener{
                            data.value = null
                        }
                    }
                }
                else{
                    data.value = null
                }
            }
    }
}