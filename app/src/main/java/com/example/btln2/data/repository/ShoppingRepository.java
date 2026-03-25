package com.example.btln2.data.repository;

import android.content.Context;
import com.example.btln2.data.local.database.AppDatabase;
import com.example.btln2.data.local.entities.*;
import java.util.List;

public class ShoppingRepository {
    private AppDatabase db;

    public ShoppingRepository(Context context) {
        db = AppDatabase.getDatabase(context);
    }

    // User operations
    public User login(String username, String password) {
        return db.userDao().login(username, password);
    }

    public User getUserById(int userId) {
        return db.userDao().getUserById(userId);
    }

    // Category operations
    public List<Category> getAllCategories() {
        return db.categoryDao().getAllCategories();
    }

    // Product operations
    public List<Product> getAllProducts() {
        return db.productDao().getAllProducts();
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return db.productDao().getProductsByCategory(categoryId);
    }

    public Product getProductById(int productId) {
        return db.productDao().getProductById(productId);
    }

    // Order operations
    public long createOrder(Order order) {
        return db.orderDao().insert(order);
    }

    public void updateOrder(Order order) {
        db.orderDao().update(order);
    }

    public Order getPendingOrder(int userId) {
        return db.orderDao().getPendingOrderByUser(userId);
    }

    // OrderDetail operations
    public void addOrderDetail(OrderDetail detail) {
        OrderDetail existing = db.orderDetailDao().getOrderDetail(detail.orderId, detail.productId);
        if (existing != null) {
            existing.quantity += detail.quantity;
            db.orderDetailDao().update(existing);
        } else {
            db.orderDetailDao().insert(detail);
        }
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        return db.orderDetailDao().getOrderDetailsByOrder(orderId);
    }
}
