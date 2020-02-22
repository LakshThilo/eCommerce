package com.lak.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, productDescription, productName, productPrice;
    private ImageView inputProductImage;
    private Button btnAddNewProduct;
    private EditText inputProductName, inputProductDescription, inputProductPrice;

    private String saveCurrentDate, saveCurrentTime;
    private String productRandomKey, downloadImageUrl;

    private StorageReference productImageRef;
    private DatabaseReference productDBRef;

    private Uri imageUri;

    private ProgressDialog loadingBar;

    private static final int galleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productDBRef = FirebaseDatabase.getInstance().getReference().child("Products");

        loadingBar = new ProgressDialog(this);

        Toast.makeText(this,categoryName,Toast.LENGTH_SHORT).show();

        inputProductImage = findViewById(R.id.select_product_image);
        btnAddNewProduct = findViewById(R.id.add_new_product);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });

        btnAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                validateProductDta();    
            }
        });

    }


    private void openGallery()
    {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


            if(requestCode==galleryPick && requestCode==RESULT_OK && data!=null)
            {
                imageUri = data.getData();
                inputProductImage.setImageURI(imageUri);
            }
    }

    private void validateProductDta()
    {
        productDescription = inputProductDescription.getText().toString();
        productPrice = inputProductPrice.getText().toString();
        productName = inputProductName.getText().toString();


        if(imageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productDescription))
        {
            Toast.makeText(this, "Please enter product description..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productPrice))
        {
            Toast.makeText(this, "Please enter product price..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(productName))
        {
            Toast.makeText(this, "Please product product name..", Toast.LENGTH_SHORT).show();
        }
        else
        {
                StoreProductInformation();
        }
    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait, while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey+ ".jpg");

        final UploadTask uploadTask = filepath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {

                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error.." + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image upload successfully.." , Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "got the product Image Url Successfully.." , Toast.LENGTH_SHORT).show();

                            //to database
                            saveProductTodDatabase();
                        }

                    }
                });
            }
        });
    }

    private void saveProductTodDatabase()
    {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", productDescription);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", productPrice);
        productMap.put("productName", productName);

        productDBRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added to the database Successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error.."+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
