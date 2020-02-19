package com.lak.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button btnCreateAccount;
    private EditText inputName, inputPassword, inputPhoneNumber;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnCreateAccount = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_name_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputPhoneNumber = findViewById(R.id.register_phoneNumber_input);

        loadingBar = new ProgressDialog(this);



        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount();
            }
        });
    }

    private void createAccount() {

        String name = inputName.getText().toString();
        String password = inputPassword.getText().toString();
        String phone = inputPhoneNumber.getText().toString();

        if(TextUtils.isEmpty(name)){

            Toast.makeText(this,"Please Enter your name...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){

            Toast.makeText(this,"Please Enter your password...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){

            Toast.makeText(this,"Please Enter your phone Number...",Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we're checking your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validateUserDetails(name, phone, password);
        }

    }

    private void validateUserDetails(final String name, final String phone, final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);

                    rootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this,"Congratulation, your account has been created.",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this,"Network error, please try again later..",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
                else
                    {
                        Toast.makeText(RegisterActivity.this,"This "+ phone +" Already exists.",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,"Please try with different phone number.",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
