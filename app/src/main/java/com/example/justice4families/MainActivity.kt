package com.example.justice4families

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import android.view.View.OnClickListener;
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.Post
import com.example.justice4families.profile.UserProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_tags_bottomsheet.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnClickListener{

    var postCollection : MutableList<Post> = ArrayList()

    var page = 1
    var isLoading = false
    val limit = 10
    var anonymous = false
    var isPressed: Array<Boolean> = Array(6) {false}
    private var tagsList: ArrayList<String> = ArrayList()
    lateinit var postAdapter: PostsAdapter
    lateinit var layoutManager : LinearLayoutManager

    //bottom sheet
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottom_sheet: LinearLayout

    private lateinit var sheetBehaviorTags: BottomSheetBehavior<LinearLayout>
    private lateinit var bottom_sheet_tags: LinearLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var postButton: TextView = findViewById(R.id.post_button)
        var titleText: TextView = findViewById(R.id.title_text)
        var postBodyText: TextView = findViewById(R.id.post_body_text)

        //bottom sheet expansion
        bottom_sheet = findViewById(R.id.bottom_sheet)
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)

        bottom_sheet_tags = findViewById(R.id.add_tags_bottomsheet)
        sheetBehaviorTags = BottomSheetBehavior.from(bottom_sheet_tags)

        bottomNav = findViewById(R.id.bottom_navigation)


        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_addpost -> {
                    sheetBehavior.state = (BottomSheetBehavior.STATE_EXPANDED)
                    postButton.isEnabled = false
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

        tag1.setOnClickListener(this)
        tag2.setOnClickListener(this)
        tag3.setOnClickListener(this)
        tag4.setOnClickListener(this)
        tag5.setOnClickListener(this)
        tag6.setOnClickListener(this)

        add_tags_button.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sheetBehaviorTags.state = BottomSheetBehavior.STATE_EXPANDED
            bottom_sheet_tags.visibility = View.VISIBLE
        }
        anon_switch.setOnCheckedChangeListener{ _, isChecked ->
            anonymous = isChecked
        }

        // disabling post button if input invalid

        titleText.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                if (s.toString().trim({ it <= ' ' }).isEmpty() || postBodyText.text.isEmpty())
                {
                    postButton.setEnabled(false)
                    postButton.setTextColor(Color.BLACK)
                }
                else
                {
                    postButton.setEnabled(true)
                    postButton.setTextColor(Color.parseColor("#19769D"))
                }
            }
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int,
                                           after:Int) {
                // TODO Auto-generated method stub
            }
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        postBodyText.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                if (s.toString().trim({ it <= ' ' }).isEmpty() || titleText.text.isEmpty())
                {
                    postButton.setEnabled(false)
                    postButton.setTextColor(Color.BLACK)
                }
                else
                {
                    postButton.setEnabled(true)
                    postButton.setTextColor(Color.parseColor("#19769D"))

                }
            }
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int,
                                           after:Int) {
                // TODO Auto-generated method stub
            }
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })

        postButton.setOnClickListener {
            Log.d("api_call", "post button clicked")
            sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)
            var subject: TextView = findViewById(R.id.title_text)
            var content: TextView = findViewById(R.id.post_body_text)
            sendPost(savedPreferences.getUserName(), subject.text.toString(),content.text.toString(),tagsList,anonymous)
            subject.text = ""
            content.text = ""

        }



        back_to_post_bottomsheet.setOnClickListener {
            bottom_sheet_tags.visibility = View.INVISIBLE
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        hide_bottomsheet.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // recycle view
        layoutManager = LinearLayoutManager(this)
        var postRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        postRecyclerView.isNestedScrollingEnabled = false
        postRecyclerView.layoutManager = layoutManager
        postAdapter = PostsAdapter(this)
        postRecyclerView.adapter = postAdapter
        postRecyclerView.layoutManager = layoutManager
        updatePost()


        findViewById<RecyclerView>(R.id.recyclerView).addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                    val total = postAdapter.itemCount
                    Log.d("total_count", total.toString())

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            page++
                            updatePost()
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
    private fun sendPost(username: String, subject: String, content: String, tags: List<String>?, anon: Boolean?)
    {
        Log.d("api_call", "SENT")
        PostApi().addPost(Post(_id = null, username=username, title=subject,text=content,tags=tags,anonymous = anon,numComments = 0, numLikes = 0, datePosted = null, media = null, likes = 0))
            .enqueue(object: Callback<ResponseBody> {


                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext,"Failed to post. Check your network connection.",Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("api_call","onResponse")
                    Log.d("api_call",  response.raw().toString())
                    Log.d("api_call",  response.body().toString())
                    if(response.code().toString() != "200")
                    {
                        Toast.makeText(applicationContext, "Failed to post. Response Code: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                    Log.d("api_call",response.toString())
                    Log.d("api_call",response.code().toString())


                }
            })
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
                        progressbar.visibility = View.GONE
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

    override fun onResume() {
        super.onResume()
        updatePost()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tag1 -> changeTagState(p0 as TextView,0)
            R.id.tag2 -> changeTagState(p0 as TextView,1)
            R.id.tag3 -> changeTagState(p0 as TextView,2)
            R.id.tag4 -> changeTagState(p0 as TextView,3)
            R.id.tag5 -> changeTagState(p0 as TextView,4)
            R.id.tag6 -> changeTagState(p0 as TextView,5)
        }
    }

    private fun changeTagState(view:TextView, index:Int){
        if(!isPressed[index]){
            view.background = resources.getDrawable(R.drawable.pressed_tags_rectangle)
            view.setTextColor(resources.getColor(R.color.white))
            isPressed[index] = true
            tagsList.add(view.text.toString())
        }
        else {
            view.background = resources.getDrawable(R.drawable.not_pressed_tags_rectangle)
            view.setTextColor(resources.getColor(R.color.purple_500))
            isPressed[index] = false
            tagsList.remove(view.text.toString())
        }
    }
}
