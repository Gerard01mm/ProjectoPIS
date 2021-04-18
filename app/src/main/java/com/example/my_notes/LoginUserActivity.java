package com.example.my_notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class LoginUserActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.label_signin).setOnClickListener(this);

        name = this.findViewById(R.id.editText_Name3);
        email = this.findViewById(R.id.editText_EmailAddress);
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
        if (R.id.button_login == view.getId()){
            /*Que hacer cuando apretamos el boton login al rellenar los datos*/
            goToMainActivity();
        }
        if (R.id.label_signin == view.getId()){
            goToRegisterUserActivity();
        }
    }
}