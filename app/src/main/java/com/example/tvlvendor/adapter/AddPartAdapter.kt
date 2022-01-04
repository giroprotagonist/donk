package com.rideyrbike1.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rideyrbike1.app.R
import com.rideyrbike1.app.listeners.AddPartClickListener
import com.rideyrbike1.app.model.Part


class AddPartAdapter (var dataSet: List<Part>,
                    private val itemClickListener: AddPartClickListener
) :
    RecyclerView.Adapter<AddPartAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.label_name)
        val type: TextView = view.findViewById(R.id.label_type)
        val life: TextView = view.findViewById(R.id.label_life)
        val description: TextView = view.findViewById(R.id.label_description)
        val price: TextView = view.findViewById(R.id.label_price)
        val quantity: TextView = view.findViewById(R.id.label_quantity)

        init {
            // Define click listener for the ViewHolder's View.
        }
        fun bind(part: Part, position: Int, clickListener: AddPartClickListener)
        {
            view.setOnClickListener {
//
                clickListener.onPartClick(part,position)


            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.part_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val part = dataSet[position]
        holder.name.text = part.name
        holder.type.text = part.type
        holder.life.text = part.life
        holder.description.text = part.description
        holder.bind(part, position, itemClickListener)

        holder.price.text = ""
        holder.quantity.text = ""
    }

    }