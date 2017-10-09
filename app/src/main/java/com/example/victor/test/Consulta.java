package com.example.victor.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Consulta extends AppCompatActivity {
    //Declarar variables
    Button btLeer;
    TextView resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        //Inicializar variables
        resultado = (TextView) findViewById(R.id.tvCodigo);
        btLeer = (Button) findViewById(R.id.btLeer);
        //Ejecutar lector
        btLeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerCodigo();
            }
        });
    }
    //Metodo para inicializar escaner
    private void leerCodigo(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(Consulta.this);
        scanIntegrator.setPrompt("Leer codigo de barras");
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();
    }

    //Obtener informacion de codigo
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult resultadoScan = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String scanContent = resultadoScan.getContents().toString();
        resultado.setText(scanContent);

    }


}
