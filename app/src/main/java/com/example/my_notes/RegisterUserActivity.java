package com.example.my_notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email, name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Button register = this.findViewById(R.id.button_register);

        email = this.findViewById(R.id.editText_Email);
        name = this.findViewById(R.id.editText_Name);
        password = this.findViewById(R.id.editText_Password);

        findViewById(R.id.button_register).setOnClickListener(this);
        findViewById(R.id.label_haveaccount).setOnClickListener(this);
    }

    private void comprovacioDades(String email, String name, String password){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            email = bundle.getString("email");
            name = bundle.getString("name");
            password = bundle.getString("password");
            Log.d("email", email);
            Log.d("name", name);
            Log.d("password", password);
        }
    }

    private void goToLoginUserActivity(){
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (R.id.button_register == view.getId()){
            goToLoginUserActivity();
        }
        if (R.id.label_haveaccount== view.getId()){
            goToLoginUserActivity();
        }
    }
}