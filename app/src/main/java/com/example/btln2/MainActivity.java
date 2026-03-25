package com.example.btln2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btln2.data.local.database.AppDatabase;
import com.example.btln2.data.local.entities.Category;
import com.example.btln2.data.local.entities.Product;
import com.example.btln2.ui.activities.LoginActivity;
import com.example.btln2.ui.activities.ProductDetailActivity;
import com.example.btln2.ui.adapters.CategoryAdapter;
import com.example.btln2.ui.adapters.ProductAdapter;
import com.example.btln2.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvCategories, rvProducts;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private AppDatabase db;
    private PreferenceManager preferenceManager;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = AppDatabase.getDatabase(this);
        preferenceManager = new PreferenceManager(this);
        
        initViews();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (preferenceManager.isLoggedIn()) {
             getSupportActionBar().setSubtitle("Chào mừng bạn quay lại!");
        } else {
             getSupportActionBar().setSubtitle("");
        }
    }

    private void initViews() {
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadData() {
        new Thread(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            List<Product> products = db.productDao().getAllProducts();
            
            runOnUiThread(() -> {
                setupCategories(categories);
                setupProducts(products);
            });
        }).start();
    }

    private void setupCategories(List<Category> categories) {
        List<Category> categoryListWithAll = new ArrayList<>();
        Category allCategory = new Category("Tất cả");
        allCategory.categoryId = -1;
        categoryListWithAll.add(allCategory);
        categoryListWithAll.addAll(categories);

        categoryAdapter = new CategoryAdapter(categoryListWithAll, category -> {
            if (category.categoryId == -1) {
                productAdapter.updateList(allProducts);
            } else {
                filterProductsByCategory(category.categoryId);
            }
        });
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupProducts(List<Product> products) {
        allProducts = products;
        productAdapter = new ProductAdapter(allProducts, product -> {
            // Kiểm tra đăng nhập trước khi xem chi tiết
            if (!preferenceManager.isLoggedIn()) {
                Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("PRODUCT_ID", product.productId);
                startActivity(intent);
            }
        });
        rvProducts.setAdapter(productAdapter);
    }

    private void filterProductsByCategory(int categoryId) {
        new Thread(() -> {
            List<Product> filteredList = db.productDao().getProductsByCategory(categoryId);
            runOnUiThread(() -> productAdapter.updateList(filteredList));
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem loginItem = menu.findItem(R.id.menu_login);
        MenuItem logoutItem = menu.findItem(R.id.menu_logout);

        if (preferenceManager.isLoggedIn()) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
        } else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_login) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            preferenceManager.logout();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}