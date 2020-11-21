package com.example.justice4families

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    val numberList : MutableList<String> = ArrayList()

    var page = 1
    var isLoading = false
    val limit = 10

    lateinit var adapter: NumAdapter
    lateinit var layoutManager : LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutManager = LinearLayoutManager(this)

        // Might need to add buildFeatures { viewBinding = true } to gradle module
        // in android {}
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = layoutManager
        getPage()

        findViewById<RecyclerView>(R.id.recyclerView).addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                    val total = adapter.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            page++
                            getPage()
                        }
                    }
                //}

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }



    fun getPage() {
        isLoading = true
        val start = (page - 1) * limit
        val end = page * limit

        for (i in start..end) {
            numberList.add("Item $i")
        }

        Handler().postDelayed({
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
            if (::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
            }
            else{
                adapter = NumAdapter(this)
                findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
            }
            isLoading = false
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
        }, 2000)
    }

    // Move to another file
    class NumAdapter(val activity : MainActivity) : RecyclerView.Adapter<NumAdapter.NumViewHolder>() {

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

        class NumViewHolder (v : View) : RecyclerView.ViewHolder(v) {
            val tvNum = v.findViewById<TextView>(R.id.tv_number)
        }

    }
}