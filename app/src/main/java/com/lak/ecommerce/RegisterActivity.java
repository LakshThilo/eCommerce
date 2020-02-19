package com.lak.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccount;
    TextView inputName, inputPassword, inputPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccount = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_phoneNumber_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputPhoneNumber = findViewById(R.id.register_phoneNumber_input);


    }
}
