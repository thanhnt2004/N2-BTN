package com.example.btln2.data.local.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.btln2.data.local.dao.*;
import com.example.btln2.data.local.entities.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "shopping_db")
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries() // For simplicity in this project
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                UserDao userDao = INSTANCE.userDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();

                // Seed Users
                userDao.insert(new User("admin", "123456", "System Admin"));
                userDao.insert(new User("user1", "123456", "Nguyen Van A"));

                // Seed Categories
                categoryDao.insert(new Category("Electronics"));
                categoryDao.insert(new Category("Fashion"));
                categoryDao.insert(new Category("Home & Garden"));

                // Seed Products
                productDao.insert(new Product("iPhone 15", 1000.0, "Latest Apple iPhone", "iphone15.jpg", 1));
                productDao.insert(new Product("Samsung S24", 900.0, "Latest Samsung Phone", "samsung_s24.jpg", 1));
                productDao.insert(new Product("T-Shirt", 20.0, "Comfortable cotton t-shirt", "tshirt.jpg", 2));
                productDao.insert(new Product("Jeans", 50.0, "Blue denim jeans", "jeans.jpg", 2));
                productDao.insert(new Product("Table Lamp", 30.0, "Modern table lamp", "lamp.jpg", 3));
            });
        }
    };
}
