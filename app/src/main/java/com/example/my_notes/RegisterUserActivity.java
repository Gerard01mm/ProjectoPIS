package com.example.my_notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener{

    //Constants
    private final String REGISTER_ERROR = "An account this that email already exists";
    private final String NOT_CHECKED = "Check the Terms of Services";
    private final String EMPTY_INPUT = "Some of the text areas are empty";

    private TextInputEditText email, name, password;
    private CheckBox privacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        this.email = (TextInputEditText) findViewById(R.id.reg_email_edit_text);
        this.name = (TextInputEditText) findViewById(R.id.reg_name_edit_text);
        this.password = (TextInputEditText) findViewById(R.id.reg_password_edit_text);
        this.privacity = (CheckBox) findViewById(R.id.checkBox_privacity);

        findViewById(R.id.continue_button).setOnClickListener(this);
        findViewById(R.id.label_haveaccount).setOnClickListener(this);
    }


    /**
     * Funcio que ens permet tornar a la activity de login
     */
    private void goToLoginUserActivity(){
        Intent intent = new Intent(this, LoginUserActivity2.class);
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

        //En cas que pulsem el botó de continue
        if (R.id.continue_button == view.getId()) {

            //En cas que els camps estiguin complerts i la checkBox marcada
            if (!this.email.getText().toString().isEmpty() && !this.password.getText().toString().isEmpty() && !this.name.getText().toString().isEmpty() && this.privacity.isChecked()) {

                //Crea un usuari amb el email i contrasenya introduits.
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(this.email.getText().toString(),
                        this.password.getText().toString()).
                        addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //Si no ha succeit cap error
                                if (task.isSuccessful()) {
                                    goToMainActivity();
                                }

                                //En cas que si
                                else {
                                    showErrorMessage(REGISTER_ERROR);
                                }
                            }
                        });
            }

            //En cas que la checkBox no estigui marcada
            else if (!this.privacity.isChecked()){
                showErrorMessage(NOT_CHECKED);
            }

            //En cas que algun camp no es trobi omplert
            else{
                showErrorMessage(EMPTY_INPUT);
            }
        }

        //En cas que pulsem l'etiqueta
        if (R.id.label_haveaccount== view.getId()){
            goToLoginUserActivity();
        }
    }


    /**
     * Aquesta funció rep un missatge d'error i el mostra a la pantalla.
     * @param error Misstage d'error
     */
    private void showErrorMessage(String error){
        Toast.makeText(RegisterUserActivity.this, error, Toast.LENGTH_SHORT).show();
    }
}