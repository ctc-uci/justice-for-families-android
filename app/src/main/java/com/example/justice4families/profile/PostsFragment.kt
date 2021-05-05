package com.example.justice4families.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.PostsAdapter
import com.example.justice4families.R
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.Post
import kotlinx.android.synthetic.main.activity_view_post.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class PostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostsAdapter
    private lateinit var userName: String
    var postCollection : MutableList<Post> = ArrayList()
    private lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.posts_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView= view.findViewById(R.id.post_recycler)
        progressbar = view.findViewById(R.id.viewpost_progressbar)
        adapter = PostsAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        userName = (activity as UserProfileActivity).username

        getUserPost()
    }

    private fun getUserPost(){
        PostApi().getPostsByUsername(userName)
            .enqueue(object : Callback<MutableList<Post>> {
                override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                    Toast.makeText(activity, t.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    println(t.message)
                }

                override fun onResponse(
                    call: Call<MutableList<Post>>,
                    response: Response<MutableList<Post>>
                ) {
                    if (response.isSuccessful) {
                        postCollection = response.body()!!
                        progressbar.visibility = View.GONE
                        adapter.setPosts(postCollection)
                    } else if (response.code() == 400) {
                        Toast.makeText(activity, "Error finding posts", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        getUserPost()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PostsFragment {
            return PostsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}