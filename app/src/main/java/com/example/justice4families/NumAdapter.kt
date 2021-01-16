package com.example.justice4families

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class NumAdapter(val activity: MainActivity) : RecyclerView.Adapter<NumAdapter.NumViewHolder>() {

    // Return the ViewHolder object
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumViewHolder {
        return NumViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_number, parent, false))
    }

    override fun getItemCount(): Int {
        return activity.numberList.size
    }

    override fun onBindViewHolder(holder: NumViewHolder, position: Int) {
        holder.tvNum.text = activity.numberList[position]
    }

    class NumViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvNum = v.findViewById<TextView>(R.id.tv_number)
    }

}
