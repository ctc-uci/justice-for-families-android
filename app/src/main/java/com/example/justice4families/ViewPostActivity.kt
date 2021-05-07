package com.example.justice4families

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.model.Comment
import com.example.justice4families.model.Post
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_view_post.*
//import kotlinx.android.synthetic.main.add_comment_bottomsheet.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ViewPostActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewPostAdapter
    private lateinit var viewModel: PostViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)


        back_to_main.setOnClickListener {
            onBackPressed()
        }

        recyclerView= findViewById(R.id.view_post_recycler)
        adapter = ViewPostAdapter(this, comment_text)
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

        comment_button.setOnClickListener {
            //save comment associated with this post
            if(comment_text.text.toString().isNotEmpty())
            {
                viewModel.addComment(Comment(null, comment_text.text.toString(),savedPreferences.username,0, post._id, getDateTime()))
                comment_text.text?.clear() //clears input box
            }

        }


        comment_text.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) showSoftKeyboard();
        }

    }


    fun showSoftKeyboard() {
        val imm: InputMethodManager? =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(comment_text ,InputMethodManager.SHOW_IMPLICIT);
    }
}

