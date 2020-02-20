package com.lak.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName;
    private ImageView imageInputProduct;
    private Button btnAddNewProduct;
    private EditText inputProductName, inputProductDescription, inputProductPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();

        Toast.makeText(this,categoryName,Toast.LENGTH_SHORT).show();

        imageInputProduct = findViewById(R.id.select_product_image);
        btnAddNewProduct = findViewById(R.id.add_new_product);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);



    }
}
