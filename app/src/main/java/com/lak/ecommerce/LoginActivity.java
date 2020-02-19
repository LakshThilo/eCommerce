package com.lak.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lak.ecommerce.Model.Users;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText inputPhoneNumber, inputPassword;

    private ProgressDialog loadingBar;

    private String parentUser = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnLogin = findViewById(R.id.login_btn);
        inputPhoneNumber = findViewById(R.id.login_phoneNumber_input);
        inputPassword = findViewById(R.id.login_password_input);

        loadingBar = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                loginUsers();
                
            }
        });

    }

    private void loginUsers()
    {
        String inputPhone = inputPhoneNumber.getText().toString();
        String inputpPassword = inputPassword.getText().toString();

        if(TextUtils.isEmpty(inputPhone))
        {
            Toast.makeText(this,"Please enter phone number..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(inputpPassword))
        {
            Toast.makeText(this,"Please enter password..", Toast.LENGTH_SHORT).show();

        }
        else
            {

                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait, while we checking the credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                allowAccessToAccount(inputPhone, inputpPassword);


            }

    }

    private void allowAccessToAccount(final String inputPhone, final String inputPassword)
    {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentUser).child(inputPhone).exists())
                {
                    Users userData = dataSnapshot.child(parentUser).child(inputPhone).getValue(Users.class);

                    if(userData.getPhone().equals(inputPhone))
                    {
                       if(userData.getPassword().equals(inputPassword))
                       {
                           Toast.makeText(LoginActivity.this,"logged in Successfully...", Toast.LENGTH_SHORT).show();
                           loadingBar.dismiss();

                           Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                           startActivity(intent);
                       }
                    }
                }
                else
                    {
                        Toast.makeText(LoginActivity.this,"Account with this "+ inputPhone +" number do not exists.",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
