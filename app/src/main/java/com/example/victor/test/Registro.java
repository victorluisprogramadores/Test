package com.example.victor.test;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Registro extends AppCompatActivity {
    Button obtener, registrar;
    TextView tv_codigo;
    CheckBox cb_modificar;
    EditText et_nombre, et_descripcion, et_modelo, et_precio, et_stock;
    Boolean ingresado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ingresado = false;
        et_nombre = (EditText) findViewById(R.id.et_nombre_producto);
        et_descripcion = (EditText) findViewById(R.id.et_descripcion_producto);
        et_modelo = (EditText) findViewById(R.id.et_modelo_producto);
        et_precio = (EditText) findViewById(R.id.et_precio_producto);
        et_stock = (EditText) findViewById(R.id.et_stock_producto);
        tv_codigo = (TextView) findViewById(R.id.tv_codigo);
        cb_modificar = (CheckBox) findViewById(R.id.cb_modificar);
        registrar = (Button) findViewById(R.id.bt_registrar);
        obtener = (Button) findViewById(R.id.bt_obtener);
        et_nombre.setEnabled(false);
        et_descripcion.setEnabled(false);
        et_modelo.setEnabled(false);
        et_precio.setEnabled(false);
        et_stock.setEnabled(false);
        cb_modificar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    registrar.setText("Modificar");
                    et_nombre.setEnabled(true);
                    et_descripcion.setEnabled(true);
                    et_modelo.setEnabled(true);
                    et_precio.setEnabled(true);
                    et_stock.setEnabled(true);
                } else {
                    registrar.setText("Registrar");
                    et_nombre.setEnabled(false);
                    et_descripcion.setEnabled(false);
                    et_modelo.setEnabled(false);
                    et_precio.setEnabled(false);
                    et_stock.setEnabled(false);
                }
            }
        });
        obtener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerCodigo();
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ingresado == true && cb_modificar.isChecked()) {
                    Log.e("Modificar", "aceptado");
                    actualizarProducto(et_nombre, et_descripcion, et_modelo, et_stock, et_precio, tv_codigo);
                }
                if (ingresado == false) {
                    registrarProducto(et_nombre, et_descripcion, et_modelo, et_stock, et_precio, tv_codigo);
                }
                if (ingresado == true && cb_modificar.isChecked() == false) {
                    Log.e("Modificar", "Debe seleccionar modificar");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Aviso: ");
                    builder.setMessage("Para actualizar un producto anteriormente ingresado, por favor, marque la opcion actualizar");
                    builder.setPositiveButton("Aceptar", null);
                    builder.show();
                }
            }
        });
    }

    //Metodo para inicializar escaner
    private void leerCodigo() {
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
        if (resultadoScan != null) {
            tv_codigo.setText(scanContent);
            existeProducto(scanContent);


        } else {
            tv_codigo.setText("No se encontro codigo");
        }
    }

    private void registrarProducto(EditText nombreL, EditText descripcionL, EditText modeloL, EditText stockL, EditText precioL, TextView codigoL) {
        String nombre, descripcion, modelo, codigoR;
        int stock;
        Double precio;
        boolean ingresar = validarProducto();
        if (ingresar == true) {
            try {
                nombre = nombreL.getText().toString();
                descripcion = descripcionL.getText().toString();
                modelo = modeloL.getText().toString();
                stock = Integer.valueOf(stockL.getText().toString());
                precio = Double.valueOf(precioL.getText().toString());
                codigoR = codigoL.getText().toString();
                AsyncHttpClient client = new AsyncHttpClient();
                String url = "https://victorluisprogramadores.000webhostapp.com/registroProductos.php";
                RequestParams params = new RequestParams();
                params.put("codigo", codigoR);
                params.put("nombre", nombre);
                params.put("descripcion", descripcion);
                params.put("modelo", modelo);
                params.put("precio", precio);
                params.put("stock", stock);

                client.post(url, params, new AsyncHttpResponseHandler() {
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
                                    builder.setPositiveButton("Aceptar", null);
                                    builder.show();

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                    builder.setTitle("Error: ");
                                    builder.setMessage("No se pudo registrar el producto");
                                    builder.setPositiveButton("Aceptar", null);
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
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                builder.setTitle("Error: ");
                builder.setMessage("No se pudo registrar el producto");
                builder.setPositiveButton("Aceptar", null);
                builder.show();
            }
        }
    }

    private boolean validarProducto() {
        boolean formCompleto = true;
        String errorVacio = "Por favor, complete los siguientes campos:\n ";
        AlertDialog.Builder ventana = new AlertDialog.Builder(Registro.this);
        ventana.setTitle("Datos requeridos!");
        ventana.setPositiveButton("Aceptar", null);
        if (tv_codigo.getText().equals("")) {
            errorVacio = errorVacio + "\nCodigo del producto.";
            formCompleto = false;
        }
        if (et_nombre.getText().toString().equals("")) {
            errorVacio = errorVacio + "\nNombre del producto.";
            formCompleto = false;
        }
        if (et_descripcion.getText().toString().equals("")) {
            errorVacio = errorVacio + "\nDescripción del producto.";
            formCompleto = false;
        }
        if (et_modelo.getText().toString().equals("")) {
            errorVacio = errorVacio + "\nModelo del producto.";
            formCompleto = false;
        }
        if (et_precio.getText().toString().equals("")) {
            errorVacio = errorVacio + "\nPrecio del producto.";
            formCompleto = false;
        }
        if (et_stock.getText().toString().equals("")) {
            errorVacio = errorVacio + "\nStock del producto.";
            formCompleto = false;
        }
        if (formCompleto == false) {

            ventana.setMessage(errorVacio);
            ventana.show();
        }
        return formCompleto;
    }

    private void existeProducto(final String codigo) {

        Log.e("Producto existe", "Inicio existeProducto");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        String url = "https://victorluisprogramadores.000webhostapp.com/existe.php";
        requestParams.put("codigo", codigo);
        final AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
        builder.setTitle("Aviso: ");
        client.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                boolean error = false;
                String mensaje = "El producto condigo: " + codigo + " ya ha sido registrado!";
                if (statusCode == 200) {
                    String codigoBd;
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            codigoBd = jsonArray.getJSONObject(i).getString("codigo");
                            if (codigo.equals(codigoBd)) {
                                error = true;
                                builder.setMessage(mensaje);
                                builder.setPositiveButton("Aceptar", null);
                                builder.show();

                                Log.e("Ingresado en funcion", ingresado.toString());

                            }
                        }

                        if (error == true) {
                            ingresado = true;
                            cb_modificar.setChecked(false);
                            et_nombre.setText("");
                            et_descripcion.setText("");
                            et_modelo.setText("");
                            et_precio.setText("");
                            et_stock.setText("");
                            Log.e("Reemplaza", "Inicia reemplazo");
                            AsyncHttpClient client = new AsyncHttpClient();
                            String url = "https://victorluisprogramadores.000webhostapp.com/consultarProducto.php";
                            RequestParams requestParams = new RequestParams();
                            requestParams.put("codigo", codigo);
                            client.post(url, requestParams, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(new String(responseBody));
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                tv_codigo.setText(jsonArray.getJSONObject(i).getString("codigo"));
                                                et_nombre.setText(jsonArray.getJSONObject(i).getString("nombre"));
                                                et_descripcion.setText(jsonArray.getJSONObject(i).getString("descripcion"));
                                                et_modelo.setText(jsonArray.getJSONObject(i).getString("modelo"));
                                                et_precio.setText(jsonArray.getJSONObject(i).getString("precio"));
                                                et_stock.setText(jsonArray.getJSONObject(i).getString("stock"));
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                    builder.setTitle("Error!: ");
                                    builder.setMessage("Error de conexión");
                                    builder.setPositiveButton("Aceptar", null);
                                    builder.show();
                                }
                            });
                        } else {


                            cb_modificar.setChecked(true);
                            et_nombre.setText("");
                            et_descripcion.setText("");
                            et_modelo.setText("");
                            et_precio.setText("");
                            et_stock.setText("");
                            ingresado = false;
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
    }

    private void actualizarProducto(EditText nombreL, EditText descripcionL, EditText modeloL, EditText stockL, EditText precioL, TextView codigoL) {
        String nombre, descripcion, modelo, codigoR;
        int stock;
        Double precio;
        boolean ingresar = validarProducto();
        if (ingresar) {
            AsyncHttpClient client = new AsyncHttpClient();
            nombre = nombreL.getText().toString();
            descripcion = descripcionL.getText().toString();
            modelo = modeloL.getText().toString();
            stock = Integer.valueOf(stockL.getText().toString());
            precio = Double.valueOf(precioL.getText().toString());
            codigoR = codigoL.getText().toString();
            String url = "https://victorluisprogramadores.000webhostapp.com/actualizarProductos.php";
            RequestParams params = new RequestParams();
            params.put("codigo", codigoR);
            params.put("nombre", nombre);
            params.put("descripcion", descripcion);
            params.put("modelo", modelo);
            params.put("precio", precio);
            params.put("stock", stock);

            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            JSONObject o = new JSONObject(new String(responseBody));
                            int actualiza = o.getInt("resultado");
                            if (actualiza == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                builder.setTitle("Exito!: ");
                                builder.setMessage("Se actualizo el producto correctamente");
                                builder.setPositiveButton("Aceptar", null);
                                builder.show();
                            }
                            if (actualiza == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                builder.setTitle("Error: ");
                                builder.setMessage("No se pudo actualizar el producto");
                                builder.setPositiveButton("Aceptar", null);
                                builder.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Error: ");
                    builder.setMessage("No se pudo actualizar el producto");
                    builder.setPositiveButton("Aceptar", null);
                    builder.show();
                }
            });
        } else {

        }


    }

}
