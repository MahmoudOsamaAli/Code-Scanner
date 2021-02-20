package com.example.codescanner.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.ArrayList;

@Entity
public class File {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;

    private String fileName;

    @TypeConverters(ProductConverter.class)
    private ArrayList<FileRow> data;

    public File() {
    }

    public File(String date, ArrayList<FileRow> data , String fName) {
        this.date = date;
        this.data = data;
        this.fileName = fName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @TypeConverters(ProductConverter.class)
    public ArrayList<FileRow> getData() {
        return data;
    }

    public void setData(ArrayList<FileRow> data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
