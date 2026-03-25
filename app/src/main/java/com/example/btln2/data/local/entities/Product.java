package com.example.btln2.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "products",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "categoryId",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE))
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int productId;
    public String productName;
    public double price;
    public String description;
    public String image;
    public int categoryId;

    public Product(String productName, double price, String description, String image, int categoryId) {
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
    }
}
