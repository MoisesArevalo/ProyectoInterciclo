package com.example.interciclofinal.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interciclofinal.R;
import com.example.interciclofinal.mainActivitys.MapsActivityCliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private Button btnRegistro;
    private TextView regitroT ;
    private EditText claveT ;
    private EditText correoT ;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        correoT = (EditText)findViewById(R.id.usuarioLogin);
        claveT = (EditText)findViewById(R.id.claveLogin);
        regitroT=(TextView) findViewById(R.id.registroLogin);
        regitroT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registro = new Intent(Login.this, Register.class);
                Login.this.startActivity(registro);
                finish();
            }
        });

        btnRegistro = (Button)findViewById(R.id.btnLogin);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo=correoT.getText().toString();
                String pass= claveT.getText().toString();
                ingresarUsuario(correo,pass);
            }
        });
    }
    private void ingresarUsuario(String correo,String password) {
        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user!=null){
            Toast.makeText(Login.this, "Usuario logeado correctamente",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, MapsActivityCliente.class));
            finish();
        }

    }
}
