package com.example.codescanner.model;

import androidx.room.TypeConverter;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductConverter {
    @TypeConverter
    public String fromBillItemLangList(ArrayList<FileRow> billItem) {
        if (billItem == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FileRow>>() {}.getType();
        return gson.toJson(billItem, type);
    }

    @TypeConverter
    public ArrayList<FileRow> toBillItemList(String billItemString) {
        if (billItemString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<FileRow>>() {}.getType();
        return gson.fromJson(billItemString, type);
    }
}