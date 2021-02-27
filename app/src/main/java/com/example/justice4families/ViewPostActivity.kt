package com.example.justice4families

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.model.Comment
import com.example.justice4families.model.Post
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_view_post.*
import kotlinx.android.synthetic.main.add_comment_bottomsheet.*


class ViewPostActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewPostAdapter
    private lateinit var viewModel: PostViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        bottomSheetBehavior = BottomSheetBehavior.from(commentBottomSheet)
        bottomSheetView = layoutInflater.inflate(R.layout.add_comment_bottomsheet, null)
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)


        back_to_main.setOnClickListener {
            onBackPressed()
        }

        recyclerView= findViewById(R.id.view_post_recycler)
        adapter = ViewPostAdapter(this, bottomSheetBehavior)
        recyclerView.adapter = adapter
        recyclerView.layoutManager= LinearLayoutManager(this)

        val post : Post? = intent.getParcelableExtra("post")
        viewModel.setPost(post!!)
        viewModel.getPost().observe(this, Observer { postItems ->
            postItems?.let{
                progressBar.visibility = View.GONE
                adapter.setItems(postItems)
            }
          }
        )

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> hidecomment.visibility = View.GONE
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        hidecomment.visibility = View.VISIBLE
                        hidecomment.setOnClickListener{
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        comment_button.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            //save comment associated with this post
                            viewModel.addComment(Comment(null, comment_text.text.toString(),savedPreferences.getUserName(),0, post._id, getDateTime()))

                        }
                    }
                    else -> null
                }
            }
        })

        val commentText: TextInputEditText = bottomSheetView.findViewById(R.id.comment_text)
        commentText.setOnFocusChangeListener{ _, hasFocus ->
            if(hasFocus) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }
}

