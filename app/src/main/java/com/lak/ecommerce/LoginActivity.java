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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lak.ecommerce.Model.Users;
import com.lak.ecommerce.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText inputPhoneNumber, inputPassword;
    private CheckBox chkbRememberMe;

    private TextView adminLink, notAdminLink;

    private ProgressDialog loadingBar;

    private String parentUser = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnLogin = findViewById(R.id.login_btn);
        inputPhoneNumber = findViewById(R.id.login_phoneNumber_input);
        inputPassword = findViewById(R.id.login_password_input);

        chkbRememberMe = findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        adminLink = (TextView) findViewById(R.id.admin_panel_link);
        notAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        loadingBar = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {

                loginUsers();
                
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                btnLogin.setText("Login Admin");
                notAdminLink.setVisibility(View.VISIBLE);
                adminLink.setVisibility(View.INVISIBLE);
                parentUser = "Admins";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnLogin.setText("Login");
                notAdminLink.setVisibility(View.INVISIBLE);
                adminLink.setVisibility(View.VISIBLE);
                parentUser = "Users";
            }
        });

    }

    private void loginUsers()
    {
        String inputPhone = inputPhoneNumber.getText().toString();
        String inputPassword = this.inputPassword.getText().toString();

        if(TextUtils.isEmpty(inputPhone))
        {
            Toast.makeText(this,"Please enter phone number..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(inputPassword))
        {
            Toast.makeText(this,"Please enter password..", Toast.LENGTH_SHORT).show();

        }
        else
            {

                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait, while we checking the credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                allowAccessToAccount(inputPhone, inputPassword);


            }

    }

    private void allowAccessToAccount(final String inputPhone, final String inputPassword)
    {

        if(chkbRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UsersPasswordKey, inputPassword);
            Paper.book().write(Prevalent.UsersPhoneKey, inputPhone);
        }

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
                           if(parentUser.equals("Admins"))
                           {
                               Toast.makeText(LoginActivity.this,"Welcome Admin, you're logged in Successfully...", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();

                               Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                               startActivity(intent);

                           }
                           else if(parentUser.equals("Users"))
                               {
                                   Toast.makeText(LoginActivity.this,"logged in Successfully...", Toast.LENGTH_SHORT).show();
                                   loadingBar.dismiss();

                                   Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                   startActivity(intent);
                               }
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
