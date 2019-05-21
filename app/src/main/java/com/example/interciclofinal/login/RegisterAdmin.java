package com.example.interciclofinal.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.interciclofinal.R;
import com.example.interciclofinal.mainActivitys.MapsActivityAdministrador;
import com.example.interciclofinal.mainActivitys.MapsActivityCliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterAdmin extends AppCompatActivity {

    private Button btnRegistro;
    private EditText usuarioT ;
    private EditText claveT ;
    private EditText correoT ;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        usuarioT = (EditText)findViewById(R.id.usuarioRegistro);
        correoT = (EditText)findViewById(R.id.correoRegistro);
        claveT = (EditText)findViewById(R.id.claveRegistro);
        btnRegistro = (Button)findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo=correoT.getText().toString();
                String pass= claveT.getText().toString();
                String usuario=usuarioT.getText().toString();
                registraUsuario(correo,pass,usuario);

            }
        });
    }
    private void registraUsuario(final String correo, final String pass, final String usuario) {

        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Map<String,Object> datos=new HashMap<>();
                            datos.put("usuario",usuario);
                            datos.put("correo",correo);
                            datos.put("pass",pass);

                            // Write a message to the database
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            String id_auth=mAuth.getCurrentUser().getUid();

                            database.child("UsuarioAdmin").child(id_auth).setValue(datos).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if (task2.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    }else{
                                        Toast.makeText(RegisterAdmin.this, "createUserWithEmail:failure",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterAdmin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        if (user!=null){
            Toast.makeText(RegisterAdmin.this, "Usuario creado correctamente",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterAdmin.this, MapsActivityAdministrador.class));
            finish();
        }


    }


}
