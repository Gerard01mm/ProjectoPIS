package com.example.my_notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginUserActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.label_signin).setOnClickListener(this);
    }

    /*Metodo para iniciar la activity de registrarse*/
    private void goToRegisterUserActivity(){
        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
    }
    private void goToMainActivity(){
        Intent intent2 = new Intent(this, MainActivity.class);
        startActivity(intent2);
    }

    @Override
    public void onClick(View view) {
        if (R.id.button_login == view.getId()){
            /*Que hacer cuando apretamos el boton login al rellenar los datos*/
        }
        if (R.id.label_signin == view.getId()){
            goToRegisterUserActivity();
        }
    }
}