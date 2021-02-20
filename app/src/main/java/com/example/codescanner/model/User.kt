package com.example.codescanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(@PrimaryKey val code: Int, val name: String, val password: String)
