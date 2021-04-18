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
        Button register = this.findViewById(R.id.continue_button);

        email = this.findViewById(R.id.reg_email_edit_text);
        name = this.findViewById(R.id.reg_name_edit_text);
        password = this.findViewById(R.id.reg_password_edit_text);

        findViewById(R.id.continue_button).setOnClickListener(this);
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
        Intent intent = new Intent(this, LoginUserActivity2.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (R.id.continue_button == view.getId()){
            goToLoginUserActivity();
        }
        if (R.id.label_haveaccount== view.getId()){
            goToLoginUserActivity();
        }
    }
}