package com.example.victor.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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
        codigo = (TextView) findViewById(R.id.tv_codigo);
        registrar = (Button) findViewById(R.id.bt_registrar);
        obtener = (Button) findViewById(R.id.bt_obtener);
        obtener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerCodigo();
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarProducto(nombre,descripcion,modelo,stock,precio,codigo);
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
        String scanContent = resultadoScan.getContents();
        if(resultadoScan != null) {
            codigo.setText(scanContent);

        }else{
            codigo.setText("No se encontro codigo");

        }
    }

    private void registrarProducto(EditText nombreL,EditText descripcionL,EditText modeloL,EditText stockL,EditText precioL,TextView codigoL){
        String nombre,descripcion,modelo,codigoR;
        int stock;
        Double precio;

        try {
            nombre = nombreL.getText().toString();
            descripcion = descripcionL.getText().toString();
            modelo = modeloL.getText().toString();
            stock = Integer.valueOf(stockL.getText().toString());
            precio = Double.valueOf(precioL.getText().toString());
            codigoR = codigoL.getText().toString();
            AsyncHttpClient client = new AsyncHttpClient();
            String url ="https://victorluisprogramadores.000webhostapp.com/registroProductos.php";
            RequestParams params = new RequestParams();
            params.put("codigo",codigoR);
            params.put("nombre",nombre);
            params.put("descripcion",descripcion);
            params.put("modelo",modelo);
            params.put("precio",precio);
            params.put("stock",stock);



            client.post(url,params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (statusCode == 200) {
                        try {
                            JSONObject o = new JSONObject(new String(responseBody));
                            boolean ingreso = o.getBoolean("resultado");
                            if (ingreso == true) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                builder.setTitle("Exito!: ");
                                builder.setMessage("Se registro el producto correctamente");
                                builder.setPositiveButton("Aceptar",null);
                                builder.show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                builder.setTitle("Error: ");
                                builder.setMessage("No se pudo registrar el producto");
                                builder.setPositiveButton("Aceptar",null);
                                builder.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
            builder.setTitle("Error: ");
            builder.setMessage("No se pudo registrar el producto");
            builder.setPositiveButton("Aceptar",null);
            builder.show();

        }



    }

    private void validarProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
        builder.setTitle("Error: ");
    }

}
