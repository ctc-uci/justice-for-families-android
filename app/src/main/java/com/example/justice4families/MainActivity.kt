package com.example.justice4families

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.Post
import com.example.justice4families.profile.UserProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_user_profile.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    var postCollection : MutableList<Post> = ArrayList()

    var page = 1
    var isLoading = false
    val limit = 10

    lateinit var postAdapter: PostsAdapter
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
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
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
            Log.d("api_call", "post button clicked")
            sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)
            var subject: TextView = findViewById(R.id.title_text)
            var content: TextView = findViewById(R.id.post_body_text)

            if(subject.text.isEmpty() || content.text.isEmpty())
                Toast.makeText(applicationContext,"Subject or Content is empty",Toast.LENGTH_LONG).show()
            else{
                var tags: List<String> = listOf("x", "y", "z")
                sendPost(savedPreferences.getUserName(), subject.text.toString(),content.text.toString(),tags,false)
                subject.text = ""
                content.text = ""
            }


        }

        // recycle view
        layoutManager = LinearLayoutManager(this)
        var postRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        postRecyclerView.layoutManager = layoutManager
        postAdapter = PostsAdapter(this)
        postRecyclerView.adapter = postAdapter
        postRecyclerView.layoutManager = layoutManager
        getPage()



        findViewById<RecyclerView>(R.id.recyclerView).addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                    val total = postAdapter.itemCount
                    Log.d("total_count", total.toString())

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            page++
                            getPage()
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })


        
        //dummy list for horizontal scroll
        var items = ArrayList<String>()
        for (i in 1..10) {
            val a = "Missed Message "
            val b = i
            items.add(a + b)
        }

        //set up horizontal recycler view
        val horizontalRecycleView = findViewById<RecyclerView>(R.id.recyclerViewHorizontal)
        val horizontalLayoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        horizontalRecycleView.setHasFixedSize(true)
        horizontalRecycleView.setItemViewCacheSize(20)
        horizontalRecycleView.layoutManager = horizontalLayoutManager
        var adapter = UpdatesAdapter(items)
        horizontalRecycleView.adapter = adapter
    }
    fun sendPost(username: String, subject: String, content: String, tags: List<String>?, anon: Boolean?)
    {
        Log.d("api_call", "SENT")
        PostApi().addPost(Post(_id = null, username=username, title=subject,text=content,tags=tags,anonymous = anon,numComments = 0, numLikes = 0, datePosted = null, media = null))
            .enqueue(object: Callback<ResponseBody> {


                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("api_call",t.message.toString())
                    Log.d("api_call","NOOO")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("api_call","success")
                    Log.d("api_call",  response.raw().toString())
                    Log.d("api_call",  response.body().toString())
                    print(response.body())
                    if(response.code().toString() == "200")
                        Toast.makeText(applicationContext,"Post Added",Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(applicationContext,"Post Failed",Toast.LENGTH_LONG).show()
                    Log.d("api_call",response.toString())
                    Log.d("api_call",response.code().toString())


                }
            })
    }

    fun getPage() {
        updatePost()

        Handler().postDelayed({
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
            if (::postAdapter.isInitialized) {
                postAdapter.notifyDataSetChanged()
            } else {
                postAdapter = PostsAdapter(this)
                findViewById<RecyclerView>(R.id.recyclerView).adapter = postAdapter
            }
            isLoading = false
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
        }, 2000)
    }


    fun updatePost()
    {

        PostApi().getAllPosts()
            .enqueue(object : Callback<MutableList<Post>> {
                override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    println(t.message)
                }

                override fun onResponse(
                    call: Call<MutableList<Post>>,
                    response: Response<MutableList<Post>>
                ) {
                    if (response.isSuccessful) {
                        postCollection = response.body()!!
                        postAdapter.setPosts(postCollection)
                    } else if (response.code() == 400) {
                        Toast.makeText(applicationContext, "Error finding posts", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })

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
