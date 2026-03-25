package com.example.btln2.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int categoryId;
    public String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
