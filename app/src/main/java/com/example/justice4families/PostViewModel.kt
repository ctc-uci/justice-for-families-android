package com.example.justice4families

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justice4families.model.Comment
import com.example.justice4families.model.Post

class PostViewModel : ViewModel(){
    private val items = mutableListOf(
            //place holders
            Post(
                "Anonymous",
                null,
                "Computer Science",
                "12/20",
                "This is a post"
            )
        ,
            Comment(
                "Monica",
                "12/20",
                "comment1"
            )
        ,
            Comment(
                "Jocelyn",
                "12/20",
                "comment2"
            )
    )

    var postItems = MutableLiveData<List<Any>>(items)


    fun addComment(comment: Comment){
        items.add(comment)
        postItems.value = items
    }
}