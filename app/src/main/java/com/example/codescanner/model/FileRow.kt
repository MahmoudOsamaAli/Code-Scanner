package com.example.codescanner.model

import androidx.room.Entity

@Entity
data class FileRow(
    val userName:String,
    val itemSerial:String,
    val date:String )
