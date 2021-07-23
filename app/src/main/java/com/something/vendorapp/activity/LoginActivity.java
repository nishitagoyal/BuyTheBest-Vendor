package com.something.vendorapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.something.vendorapp.R;
import com.something.vendorapp.model.Shared;

public class    LoginActivity extends AppCompatActivity {

    EditText usernameET, passwordET;
    Button loginButton;
    TextView registerTV, forgotPassTV;
    Shared shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = usernameET.getText().toString();
            String password = passwordET.getText().toString();
            if(username.equalsIgnoreCase("User1") && password.equalsIgnoreCase("User123"))
            {
                shared.setFirstTimeLaunched(false);
                Toast.makeText(getApplicationContext(),"Login Successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else
                Toast.makeText(getApplicationContext(),"Please check username and password.", Toast.LENGTH_LONG).show();

        }
    });
        registerTV.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //do something
        }
    });
}

    private void initViews() {
        usernameET = findViewById(R.id.username_et);
        passwordET = findViewById(R.id.password_et);
        loginButton = findViewById(R.id.login_button);
        registerTV = findViewById(R.id.register_text);
        forgotPassTV = findViewById(R.id.forget_pass_et);
        shared = new Shared(LoginActivity.this);
    }
}