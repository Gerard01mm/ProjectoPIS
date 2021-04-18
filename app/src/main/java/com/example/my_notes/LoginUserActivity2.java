package com.example.my_notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Pattern;

public class LoginUserActivity2 extends AppCompatActivity implements View.OnClickListener{

    TextInputEditText name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user2);

        findViewById(R.id.next_button).setOnClickListener(this);
        findViewById(R.id.label_signin2).setOnClickListener(this);

        name = this.findViewById(R.id.reg_name_edit_text);
        email = this.findViewById(R.id.reg_email_edit_text);

    }

    /*Metodo para iniciar la activity de registrarse*/
    private void goToRegisterUserActivity(){
        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
    }

    private void goToMainActivity(){
        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.putExtra("usuario", name.getText().toString());
        intent2.putExtra("email", email.getText().toString());
        startActivity(intent2);
    }

    @Override
    public void onClick(View view) {
        if (R.id.next_button == view.getId()){
            goToMainActivity();
        }
        if (R.id.label_signin2 == view.getId()){
            goToRegisterUserActivity();
        }
    }

}