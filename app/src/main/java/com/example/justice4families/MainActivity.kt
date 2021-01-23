package com.example.justice4families

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

    lateinit var adapter : NumAdapter
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

        findViewById<Button>(R.id.close_post_modal_button).setOnClickListener {
            sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)
        }

        findViewById<Button>(R.id.post_button).setOnClickListener {
            sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)
        }


        layoutManager = LinearLayoutManager(this)

        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = layoutManager
        getPage()

        findViewById<RecyclerView>(R.id.recyclerView).addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                val total = adapter.itemCount

                if (!isLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        page++
                        getPage()
                    }
                }
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