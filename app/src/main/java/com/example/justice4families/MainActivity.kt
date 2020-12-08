package com.example.justice4families

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONObject
import java.lang.Exception
import java.net.URL


class MainActivity : AppCompatActivity() {

    val numberList : MutableList<String> = ArrayList()

    var page = 1
    var isLoading = false
    val limit = 10

    lateinit var adapter: NumAdapter
    lateinit var layoutManager : LinearLayoutManager

    //bottom sheet
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottom_sheet: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //bottom sheet expansion
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        bottomNav = findViewById(R.id.bottom_navigation)

        sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_addpost -> {
                    sheetBehavior.state = (BottomSheetBehavior.STATE_EXPANDED)
                }
                R.id.ic_profile -> {
                    sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)
                }
                R.id.ic_search -> {
                    sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)
                }
            }
            true
        }

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

            updatePost(i.toString())

        }

        Handler().postDelayed({
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
            if (::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
            } else {
                adapter = NumAdapter(this)
                findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
            }
            isLoading = false
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
        }, 2000)
    }

    fun updatePost(i: String)
    {
        var url = "https://jsonplaceholder.typicode.com/todos/" + i
        val thread = Thread {
            try {
                var dummyJson = URL(url).readText()
                val jsonObj = JSONObject(dummyJson)
                numberList.add(jsonObj.get("title").toString() )
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        thread.start()
    }


    // Move to another file
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.topnav_menu, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.top_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                Toast.makeText(this@MainActivity, "looking for $query", Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}