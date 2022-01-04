package com.rideyrbike1.app.view

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rideyrbike1.app.R
import com.rideyrbike1.app.adapter.AddPartAdapter
import com.rideyrbike1.app.listeners.AddPartClickListener
import com.rideyrbike1.app.model.Part
import com.rideyrbike1.app.viewmodel.AddPartViewModel
import com.rideyrbike1.app.viewmodel.ViewPartsViewModel


class AddPartActivity : AppCompatActivity(), AddPartClickListener {
    private var partsViewModel: AddPartViewModel = AddPartViewModel()
    private lateinit var priceText: String
    private lateinit var quantityText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_part)
        val partsRecyclerView = findViewById<RecyclerView>(R.id.recyclerview_parts)
        partsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        val addPartsAdapter = AddPartAdapter(emptyList(), this)

        partsRecyclerView.adapter = addPartsAdapter

        partsViewModel.data.observe(this, Observer {
            Log.d("PART", "UPDATING")
            addPartsAdapter.dataSet = it
            addPartsAdapter.notifyDataSetChanged()
        })


        partsViewModel.loadParts()

    }



    override fun onPartClick(part: Part, position: Int) {
        Log.d("Add Part","Enter Info")
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Add Part in Inventory")
        alert.setMessage("Enter quantity and price of Item.")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val price = EditText(this)
        price.setSingleLine()
        price.hint = "Enter price of each part"
        layout.addView(price)

        val quantity = EditText(this)
        quantity.setSingleLine()
        quantity.hint = "Enter quantity of items"
        layout.addView(quantity)

        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("Proceed") { _, _ ->
            priceText = price.text.toString()
            quantityText = quantity.text.toString()

            Log.i("xxx",priceText )
            Log.i("xxx",quantityText )

            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show()

            add(part,priceText.toInt(),quantityText.toInt())
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()

    }
    fun add(part:Part,price :Int,quantity :Int){
        partsViewModel.addPart(part,price,quantity)
    }


}

/*class AddPartActivity : AppCompatActivity(), addPart.OnClickListener {
    private lateinit var viewViewModel: ViewPartsViewModel
    private lateinit var m_Text: String
    private lateinit var l_number: String
    var flag = false
    private lateinit var part: Part

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_part)

        var recyclerView = findViewById<RecyclerView>(R.id.recyclerview_parts)
        val recyclerViewAdapter = addPart()
        viewViewModel = ViewPartsViewModel()
        viewViewModel.data.observe(this, Observer {

            recyclerViewAdapter.setData(viewViewModel.data, this.applicationContext, this)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = recyclerViewAdapter
        })


    }

    override fun onVehicleClick(part: Part) {

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter Kilometers Driven")

        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_NORMAL
        builder.setView(input)

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which -> m_Text = input.text.toString() ; lisence_number(part)})
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()


    }


    fun Back(view: View) {
        finish()
    }

    fun lisence_number(vehicle: Part){
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter Lisence Number")

        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_NORMAL
        builder.setView(input)

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which -> l_number = input.text.toString() ; add(vehicle)})
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    fun add(vehicle: Part){

    }


}*/

