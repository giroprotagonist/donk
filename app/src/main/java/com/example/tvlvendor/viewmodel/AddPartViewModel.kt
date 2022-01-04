package com.rideyrbike1.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rideyrbike1.app.model.Part
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddPartViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var data = MutableLiveData<List<Part>>()

    fun loadParts(){
        auth = FirebaseAuth.getInstance()
        val parts = mutableListOf<Part>()

        db.collection("Part").get().addOnSuccessListener { result ->
            for (document in result) {
                var a=document.data
                Log.d("Id",document.id)
                Log.d("OUTPUT", a["description"].toString())
                var id:String?= document.id
                var name:String?= a["name"] as String
                var type:String?= a["type"] as String
                var description:String?= a["description"].toString()
                var life:String?= a["life"].toString()
                parts.add(Part(id, name, description, life, type,0,0))
                data.value=parts
            }
        }
        .addOnFailureListener{
            Log.d("ERROR", it.toString())
            data.value = null
        }
    }
    fun addPart(part: Part, price:Int, quantity:Int){
        val uid: String = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        val addData = hashMapOf(
            "id" to part.id,
            "price" to price,
            "quantity" to quantity
        )

        db.collection("Vendor").document(uid).update("inventory", FieldValue.arrayUnion(addData))
        Log.d("OUTPUT", uid);

    }

}