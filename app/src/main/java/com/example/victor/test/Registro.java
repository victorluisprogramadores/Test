package com.example.victor.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class Registro extends AppCompatActivity {
    Button obtener,registrar;
    TextView codigo;
    EditText nombre,descripcion,modelo,precio,stock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        nombre = (EditText) findViewById(R.id.et_nombre_producto);
        descripcion = (EditText) findViewById(R.id.et_descripcion_producto);
        modelo = (EditText) findViewById(R.id.et_modelo_producto);
        precio = (EditText) findViewById(R.id.et_precio_producto);
        stock = (EditText) findViewById(R.id.et_stock_producto);
        codigo = (TextView) findViewById(R.id.tvCodigo);
        registrar = (Button) findViewById(R.id.btRegistrar);
        obtener = (Button) findViewById(R.id.btObtener);
        obtener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerCodigo();
            }
        });
    }

    //Metodo para inicializar escaner
    private void leerCodigo(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(Registro.this);
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
        codigo.setText(scanContent);

    }

    private void registrarProducto(EditText nombreL,EditText descripcionL,EditText modeloL,EditText stockL,EditText precioL,TextView codigoL){
        String nombre,descripcion,modelo,codigoR;
        int stock;
        Double precio;

        nombre = nombreL.getText().toString();
        descripcion = descripcionL.getText().toString();
        modelo = modeloL.getText().toString();
        stock = Integer.valueOf(stockL.getText().toString());
        precio = Double.valueOf(precioL.getText().toString());
        codigoR = codigoL.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        String url ="http://srvpruebas2016.esy.es/android/registro.php";
        RequestParams params = new RequestParams();
        params.put("",descripcion);
        params.put("Nombre",nombre);
        params.put("Descripcion",descripcion);
        params.put("Modelo",modelo);
        params.put("Precio",precio);
        params.put("Stock",stock);





    }

}
