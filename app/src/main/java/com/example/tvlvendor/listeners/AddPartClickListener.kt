package com.rideyrbike1.app.listeners

import com.rideyrbike1.app.model.Part

interface AddPartClickListener {
    fun onPartClick(part: Part, position:Int)
}