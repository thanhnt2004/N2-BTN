package com.example.btln2.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int orderId;
    public int userId;
    public String createdDate;
    public String status; // "Pending" or "Paid"

    public Order(int userId, String createdDate, String status) {
        this.userId = userId;
        this.createdDate = createdDate;
        this.status = status;
    }
}
