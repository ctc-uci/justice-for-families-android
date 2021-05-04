package com.example.justice4families

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.View.OnClickListener;
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.UpdatesRequest
import com.example.justice4families.model.CommentUpdate
import com.example.justice4families.model.Post
import com.example.justice4families.model.Update
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
import java.time.LocalDateTime
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnClickListener{
    lateinit var swipeContainer: SwipeRefreshLayout
    var postCollection : MutableList<Post> = ArrayList()

    var page = 1
    var isLoading = false
    private val numMaxTags = 2
    var anonymous = false
    private val popularTagsList: Array<String> = arrayOf(
        "community",
        "covid19",
        "health",
        "resources" ,
        "new facilities",
        "rules"
    )
    var popularTagIsPressed: MutableMap<String, Boolean> = mutableMapOf()
    private var tagsList: ArrayList<String> = ArrayList()
    private var popularTags: ArrayList<TextView> = ArrayList()
    private var selectedTags: ArrayList<TextView> = ArrayList()
    lateinit var postAdapter: PostsAdapter
    lateinit var layoutManager : LinearLayoutManager
    lateinit var updateAdapter: UpdatesAdapter

    //bottom sheet
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottom_sheet: LinearLayout

    private lateinit var sheetBehaviorTags: BottomSheetBehavior<LinearLayout>
    private lateinit var bottom_sheet_tags: LinearLayout

    private lateinit var postButton: TextView
    private lateinit var titleText: TextView
    private lateinit var postBodyText: TextView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postButton = findViewById(R.id.post_button)
        titleText = findViewById(R.id.title_text)
        postBodyText = findViewById(R.id.post_body_text)

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
                R.id.ic_home -> {
                    sheetBehavior.state = (BottomSheetBehavior.STATE_HIDDEN)
                }
                R.id.ic_notification -> {
                    sheetBehavior.state= (BottomSheetBehavior.STATE_HIDDEN)
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // Tags
        selectedTags.add(selected_tag1 as TextView)
        selectedTags.add(selected_tag2 as TextView)

        popularTags.add(tag1 as TextView)
        popularTags.add(tag2 as TextView)
        popularTags.add(tag3 as TextView)
        popularTags.add(tag4 as TextView)
        popularTags.add(tag5 as TextView)
        popularTags.add(tag6 as TextView)

        popularTagIsPressed = popularTagsList.map { it to false }.toMap().toMutableMap()
        initPopularTags(popularTags, popularTagsList)

        tag1.setOnClickListener(this)
        tag2.setOnClickListener(this)
        tag3.setOnClickListener(this)
        tag4.setOnClickListener(this)
        tag5.setOnClickListener(this)
        tag6.setOnClickListener(this)
        selected_tag1.setOnClickListener(this)
        selected_tag2.setOnClickListener(this)
        updateSelectedTagsHeader()

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
                if (s.toString().trim({ it <= ' ' }).isEmpty() || postBodyText.text.isEmpty()
                    || tagsList.isEmpty())
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
                if (s.toString().trim({ it <= ' ' }).isEmpty() || titleText.text.isEmpty()
                    || tagsList.isEmpty())
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
            sendPost(savedPreferences.username, subject.text.toString(),content.text.toString(),tagsList,anonymous)
            subject.text = ""
            content.text = ""

            // Reset tags
            tagsList.clear()
            updateSelectedTagsHeader()
            selectedTags.forEach {
                it.isPressed = false
                it.isInvisible = true
                it.text = ""
            }
            popularTags.forEach {
                it.isPressed = false
                it.background = resources.getDrawable(R.drawable.not_pressed_tags_rectangle)
                it.setTextColor(resources.getColor(R.color.purple_500))
                popularTagIsPressed[it.text.toString()] = false
            }
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
        swipeContainer =  findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout);
        var postRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        postRecyclerView.isNestedScrollingEnabled = false
        postRecyclerView.layoutManager = layoutManager
        postAdapter = PostsAdapter(this)
        postRecyclerView.adapter = postAdapter
        postRecyclerView.layoutManager = layoutManager
        updatePost()

        swipeRefreshLayout.setOnRefreshListener {
            Log.d("checking1", "Refreshing feed")
            updatePost()
            loadMissedUpdates(savedPreferences.username)
            swipeRefreshLayout.isRefreshing = false
        }

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

        //set up horizontal recycler view
        val horizontalRecycleView = findViewById<RecyclerView>(R.id.recyclerViewHorizontal)
        val horizontalLayoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        horizontalRecycleView.setHasFixedSize(true)
        horizontalRecycleView.setItemViewCacheSize(20)
        horizontalRecycleView.layoutManager = horizontalLayoutManager
        updateAdapter = UpdatesAdapter()
        loadMissedUpdates(savedPreferences.username)
        horizontalRecycleView.adapter = updateAdapter

    }

    private fun sendPost(username: String, subject: String, content: String, tags: List<String>?, anon: Boolean?)
    {
        Log.d("api_call", "SENT")
        var name = username
        PostApi().addPost(Post(_id = null, username=name, title=subject,text=content,tags=tags,anonymous = anon,numComments = 0, numLikes = 0, datePosted = null, media = null))
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


    fun updatePost() {

        PostApi().getAllPosts()
            .enqueue(object : Callback<MutableList<Post>> {
                override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    Log.e("Get posts", t.message.toString())
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
        when(p0?.id) {
            R.id.tag1 -> changeTagState(p0 as TextView)
            R.id.tag2 -> changeTagState(p0 as TextView)
            R.id.tag3 -> changeTagState(p0 as TextView)
            R.id.tag4 -> changeTagState(p0 as TextView)
            R.id.tag5 -> changeTagState(p0 as TextView)
            R.id.tag6 -> changeTagState(p0 as TextView)
            R.id.selected_tag1 -> changeTagState(p0 as TextView)
            R.id.selected_tag2 -> changeTagState(p0 as TextView)
        }
    }

    private fun changeTagState(view:TextView) {
        val tagText = view.text.toString()
        if (!popularTagIsPressed[tagText]!! && popularTagIsPressed.filterValues { it }.size < numMaxTags) {
            view.background = resources.getDrawable(R.drawable.pressed_tags_rectangle)
            view.setTextColor(resources.getColor(R.color.white))
            view.isPressed = true
            popularTagIsPressed[tagText] = true
            tagsList.add(tagText)

            val selected = selectedTags[tagsList.size - 1]
            selected.text = view.text
            selected.isPressed = true
            selected.isVisible = true

            if (!(postBodyText.text.isEmpty() || titleText.text.isEmpty())) {
                postButton.isEnabled = true
                postButton.setTextColor(resources.getColor(R.color.purple_500))
            }
        } else {
            if (view in selectedTags) {
                popularTags.forEach {
                    if (it.text == tagText) {
                        it.isPressed = false
                        it.background = resources.getDrawable(R.drawable.not_pressed_tags_rectangle)
                        it.setTextColor(resources.getColor(R.color.purple_500))
                    }
                }
            } else {
                view.background = resources.getDrawable(R.drawable.not_pressed_tags_rectangle)
                view.setTextColor(resources.getColor(R.color.purple_500))
                view.isPressed = false
            }

            selectedTags.forEachIndexed { index, it ->
                if (it.text == tagText) {
                    if (index == 0 && !selectedTags[1].isInvisible) {
                        // Move the second tag to the position of the first
                        it.text = selectedTags[1].text
                        it.isPressed = true
                        it.background = resources.getDrawable(R.drawable.pressed_tags_rectangle)
                        it.setTextColor(resources.getColor(R.color.white))
                        selectedTags[1].isPressed = false
                        selectedTags[1].isInvisible = true
                        selectedTags[1].text = ""
                    } else {
                        it.isPressed = false
                        it.isInvisible = true
                        it.text = ""
                    }
                }
            }

            popularTagIsPressed[tagText] = false
            tagsList.remove(tagText)

            if (tagsList.isEmpty()) {
                postButton.isEnabled = false
                postButton.setTextColor(Color.BLACK)
            }
        }
        updateSelectedTagsHeader()
    }

    private fun initPopularTags(popularTags: ArrayList<TextView>, popularTagsList: Array<String>) {
        popularTagsList.forEachIndexed { index, text ->
            try {
                popularTags[index].text = text
            } catch (e: IndexOutOfBoundsException) {
                return
            }
        }
    }

    private fun updateSelectedTagsHeader() {
        currently_selected_title.text = getString(R.string.selected_tags_section_title, tagsList.size, numMaxTags)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMissedUpdates(username: String) {

        val currentDate: LocalDateTime = LocalDateTime.now()
        val startingFrom: LocalDateTime = currentDate.minusDays(2)
        val request = UpdatesRequest(username, startingFrom.toString())
        Log.d("missed activity", "$username $startingFrom")

        PostApi().getMissedActivity(request)
            .enqueue(object : Callback<Update> {
                override fun onFailure(call: Call<Update>, t: Throwable) {
                    Log.d("missed updates", t.message.toString())
                }

                override fun onResponse(
                    call: Call<Update>,
                    response: Response<Update>
                ) {
                    if(response.isSuccessful) {
                        Log.d("missed updates", "api call successful")
                        val updatesCollection = response.body()!!
                        Log.d("missed updates", updatesCollection.comments.size.toString())
                        updateAdapter.setUpdates(updatesCollection.comments, username)

                    }
                }
            })
    }

    private fun retrieveUpdatesFromAPI(request: UpdatesRequest) : ArrayList<CommentUpdate> {

        var updatesCollection = ArrayList<CommentUpdate>()

        PostApi().getMissedActivity(request)
            .enqueue(object : Callback<Update> {
                override fun onFailure(call: Call<Update>, t: Throwable) {
                    Log.d("missed updates", t.message.toString())
                }

                override fun onResponse(
                    call: Call<Update>,
                    response: Response<Update>
                ) {
                    if(response.isSuccessful) {
                        Log.d("missed updates", "api call successful")
                        updatesCollection = response.body()!!.comments
                        Log.d("missed updates", updatesCollection.size.toString())
                    }
                }
            })
        Log.d("missed updates", updatesCollection.toString())
        return updatesCollection
    }
}
