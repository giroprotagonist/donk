package com.rideyrbike1.app.viewmodel

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rideyrbike1.app.model.Part
import com.rideyrbike1.app.model.UserVehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ViewPartsViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var data = MutableLiveData<List<Part>>()

    fun loadParts(){
        auth = FirebaseAuth.getInstance()
        db.collection("Vendor").document(auth.currentUser?.uid!!).get().addOnSuccessListener { res ->
            if(res["inventory"] != null) {

                val parts = mutableListOf<Part>()
                val partsIds = mutableListOf<String>()

                @Suppress("UNCHECKED_CAST")
                val partsInfo = res["inventory"] as ArrayList<HashMap<String, Any>>?
                if(partsInfo != null) {
                    for (part in partsInfo) {
                        partsIds.add(part["id"].toString())
                        parts.add(Part(part["id"] as String?, quantity = part["quantity"] as Number?, price = part["price"] as Number?))
                    }
                    val idRef = com.google.firebase.firestore.FieldPath.documentId()
                    db.collection("Part").whereIn(idRef, partsIds).get().addOnSuccessListener { partSnapshot ->
                        for (doc in partSnapshot){
                            val index = partsIds.indexOf(doc.id)
                            if(index != -1)
                                parts[index] = Part(doc.id, doc["name"] as String?, doc["description"] as String?, doc["life"] as String?, doc["type"] as String?, parts[index].price, parts[index].quantity)

                        }
                        data.value = parts
                    }.addOnFailureListener {
                        data.value = parts
                    }
                }
            }
        }.addOnFailureListener{
            Log.d("ERROR", it.toString())
            data.value = null
        }
    }
}