package com.example.my_notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Pattern;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginUserActivity2 extends AppCompatActivity implements View.OnClickListener{

    //Constants
    private final String LOGIN_ERROR = "Name, email or password are not correct";
    private final String EMPTY_INPUT = "Text area is empty";

    private TextInputEditText name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user2);

        findViewById(R.id.next_button).setOnClickListener(this);
        findViewById(R.id.label_signin2).setOnClickListener(this);

        this.name = (TextInputEditText) findViewById(R.id.name_edit_text);
        this.email = (TextInputEditText) findViewById(R.id.email_edit_text);
        this.password = (TextInputEditText) findViewById(R.id.password_edit_text);

    }

    /*Metodo para iniciar la activity de registrarse*/
    private void goToRegisterUserActivity(){
        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
    }


    /**
     * Funció que permet passar del loguin a la activitat principal.
     */
    private void goToMainActivity(){
        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.putExtra("usuario", name.getText().toString());
        intent2.putExtra("email", email.getText().toString());
        startActivity(intent2);
    }

    @Override
    public void onClick(View view) {

        //En cas que pulsem el botond e continue
        if (R.id.next_button == view.getId()){
            boolean error = false;

            //En el cas que estigui buit el EditText de l'email
            if (this.email.getText().toString().isEmpty()){
                email.setError(EMPTY_INPUT);
                error = true;

                //En el cas que no estigui buit el EditText de l'email, però hagui sigut introduit amb un format incorrecte.
            }else{
                boolean emailCorrect = true;
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                emailCorrect = pattern.matcher(this.email.getText().toString()).matches();
                if(!emailCorrect){
                    email.setError("Incorrect email format");
                    error = false;
                }
            }
            //En el cas que estigui buit el EditText de la contrassenya.
            if (this.password.getText().toString().isEmpty()){
                password.setError(EMPTY_INPUT);
                error = true;
            }
            //En el cas que estigui buit el EditText del nom d'usuari.
            if (this.name.getText().toString().isEmpty()){
                name.setError(EMPTY_INPUT);
                error = true;
            }
            //Si no hi ha cap error
            if(!error){
                FirebaseAuth.getInstance().
                        signInWithEmailAndPassword(this.email.getText().toString(),
                                this.password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //En cas que trobem l'usuari
                        if (task.isSuccessful()){
                            goToMainActivity();
                        }
                        //En qualsevol altre cas
                        else{
                            showErrorMessage(LOGIN_ERROR);
                        }
                    }
                });
            }
        }
        //En cas que pulsem l'etiqueta de signIn
        if (R.id.label_signin2 == view.getId()){
            goToRegisterUserActivity();
        }
    }

    private void showErrorMessage(String error){
        Toast.makeText(LoginUserActivity2.this, error, Toast.LENGTH_SHORT).show();
    }

}