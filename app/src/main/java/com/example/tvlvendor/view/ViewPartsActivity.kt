package com.rideyrbike1.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rideyrbike1.app.R
import com.rideyrbike1.app.adapter.PartsAdapter
import com.rideyrbike1.app.listeners.PartClickListener
import com.rideyrbike1.app.model.Part
import com.rideyrbike1.app.viewmodel.ViewPartsViewModel

class ViewPartsActivity : AppCompatActivity(), PartClickListener {

    private var partsViewModel: ViewPartsViewModel = ViewPartsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_parts)

        val partsRecyclerView = findViewById<RecyclerView>(R.id.recyclerview_parts)
        partsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        val partsAdapter = PartsAdapter(emptyList(), this)

        partsRecyclerView.adapter = partsAdapter

        partsViewModel.data.observe(this, Observer {
            Log.d("PART", "UPDATING")
            partsAdapter.dataSet = it
            partsAdapter.notifyDataSetChanged()
        })


        partsViewModel.loadParts()
    }


}