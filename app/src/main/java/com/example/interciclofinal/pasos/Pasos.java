package com.example.interciclofinal.pasos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interciclofinal.R;
import com.example.interciclofinal.login.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Pasos extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    EditText distancia;
    private   TextView ps;

    DecimalFormat df = new DecimalFormat("#.00");
    boolean corre= false;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasos);
        sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        ps=(TextView) findViewById(R.id.tv_pasos);
        distancia=(EditText) findViewById(R.id.tv_distancia);



        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
    }
    @Override
    protected void onResume(){
        super.onResume();
        corre=true;
        Sensor countSensor= sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(countSensor!=null){
            sensorManager.registerListener(this,countSensor, SensorManager.SENSOR_DELAY_UI);
        }else{
            Toast.makeText(this,"No se encontro sensor de pasos",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        corre=false;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(corre){
                System.out.println("----------------------------> ENTRA"+event.values[0]);
                ps.setText(String.valueOf(event.values[0]));
                distancia.setText(df.format(event.values[0]*0.716)+" m ");
                ///desde aqui envia los datos a la base de datos
                actualizar(event.values[0],(event.values[0]*0.716));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private float npasos=0;
    ////ingresa los datos a la base cada 10 pasos

    private void actualizar(float valor,double distancia){
        int a= (int)valor;
        if(valor>=npasos){
            ///actualizar
            ///Toast.makeText(this, "Se almacena", Toast.LENGTH_SHORT).show();
            npasos=valor+10;
            Map<String, Object> posicion=new HashMap<>();
            posicion.put("id",mAuth.getCurrentUser().getUid());
            posicion.put("pasos",String.valueOf(valor));
            posicion.put("distancia",String.valueOf(distancia));

            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            String id_auth=mAuth.getCurrentUser().getUid();

            database.child("Pasos").child(id_auth).setValue(posicion);
        }
    }
}

