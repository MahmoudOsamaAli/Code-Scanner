package com.example.codescanner.data

import androidx.room.*
import com.example.codescanner.model.File
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Query("SELECT * FROM File")
    fun getAll(): Flow<List<File>>

    @Delete
    fun deleteFile(file: File)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file:File)

    @Query("DELETE FROM File")
    fun clearTable()

}