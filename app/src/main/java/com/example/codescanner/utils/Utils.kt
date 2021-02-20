package com.example.codescanner.utils

import com.example.codescanner.model.User

object Utils {

    var currentUser:User? = null

    fun getUsers() : ArrayList<User>{
        val users = ArrayList<User>()
        users.add(User(123456 , "Mahmoud Osama Ali" , "12345"))
        users.add(User(654321 , "Ali Osama Ali" , "12345"))
        users.add(User(159357 , "Mohamed Hussein Mohamed" , "12345"))
        users.add(User(951753 , "Ahmed Hussein Mohamed" , "12345"))
        return users
    }
}