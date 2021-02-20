package com.example.codescanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.codescanner.model.File
import com.example.codescanner.model.User

@Database(entities = [File::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

    companion object {
        var db: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return if (db == null) {
                db = Room.databaseBuilder(context, AppDatabase::class.java, "Scanner-Database")
                    .build()
                db!!
            } else db!!
        }
    }

}