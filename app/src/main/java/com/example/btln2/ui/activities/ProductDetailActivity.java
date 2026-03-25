package com.example.btln2.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btln2.R;
import com.example.btln2.data.local.database.AppDatabase;
import com.example.btln2.data.local.entities.Order;
import com.example.btln2.data.local.entities.OrderDetail;
import com.example.btln2.data.local.entities.Product;
import com.example.btln2.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvProductDescription;
    private Button btnAddToCart;

    private AppDatabase db;
    private PreferenceManager preferenceManager;
    private int productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = AppDatabase.getDatabase(this);
        preferenceManager = new PreferenceManager(this);

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            finish();
            return;
        }

        initViews();
        loadProductData();

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddToCart();
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void loadProductData() {
        product = db.productDao().getProductById(productId);
        if (product != null) {
            tvProductName.setText(product.productName);
            tvProductPrice.setText(String.format(Locale.getDefault(), "$%.2f", product.price));
            tvProductDescription.setText(product.description);
        }
    }

    private void handleAddToCart() {
        if (!preferenceManager.isLoggedIn()) {
            Toast.makeText(this, getString(R.string.login_to_purchase), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        int userId = preferenceManager.getUserId();
        Order pendingOrder = db.orderDao().getPendingOrderByUser(userId);
        int orderId;

        if (pendingOrder == null) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Order newOrder = new Order(userId, currentDate, "Pending");
            orderId = (int) db.orderDao().insert(newOrder);
        } else {
            orderId = pendingOrder.orderId;
        }

        if (product != null) {
            OrderDetail existingDetail = db.orderDetailDao().getOrderDetail(orderId, productId);
            if (existingDetail != null) {
                existingDetail.quantity += 1;
                db.orderDetailDao().update(existingDetail);
            } else {
                OrderDetail newDetail = new OrderDetail(orderId, productId, 1, product.price);
                db.orderDetailDao().insert(newDetail);
            }
            Toast.makeText(this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
        }
    }
}
