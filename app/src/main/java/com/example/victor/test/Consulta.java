package com.example.victor.test;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class Consulta extends AppCompatActivity {
    //Declarar variables
    Button bt_leer;
    TextView tv_codigo, tv_nombre, tv_descripcion, tv_modelo, tv_precio, tv_stock;
    ProgressBar pb_carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        //Inicializar variables
        tv_codigo = (TextView) findViewById(R.id.tv_codigo);
        tv_nombre = (TextView) findViewById(R.id.tv_nombre);
        tv_descripcion = (TextView) findViewById(R.id.tv_descripcion);
        tv_modelo = (TextView) findViewById(R.id.tv_modelo);
        tv_precio = (TextView) findViewById(R.id.tv_precio);
        tv_stock = (TextView) findViewById(R.id.tv_stock);
        bt_leer = (Button) findViewById(R.id.bt_consultar);
        pb_carga = (ProgressBar) findViewById(R.id.pb_carga);
        //Ejecutar lector
        bt_leer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerCodigo();
            }
        });
    }

    //Metodo para inicializar escaner
    private void leerCodigo() {
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
        String scanContent = resultadoScan.getContents();
        if (resultadoScan != null) {
            consultarProducto(scanContent);
            pb_carga.setProgress(25);
        } else {

            pb_carga.setProgress(0);
        }

    }

    private void consultarProducto(final String codigo) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://victorluisprogramadores.000webhostapp.com/consultarProducto.php";
        RequestParams params = new RequestParams();
        params.put("codigo", codigo);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONArray producto = new JSONArray(new String(responseBody));
                        String codi = producto.getJSONObject(0).getString("codigo");
                        if (!TextUtils.isEmpty(codi) || !codi.equals(null) || codi.equals(codigo)) {

                            pb_carga.setProgress(50);
                            cargarProducto(codigo);
                            Log.e("Codigo enviado: ", codigo);

                        } else {
                            pb_carga.setProgress(0);
                            Log.e("Resultado: ", codi);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Consulta.this);
                builder.setTitle("Error!: ");
                builder.setMessage("Error de conexión");
                builder.setPositiveButton("Aceptar", null);
                builder.show();
                pb_carga.setProgress(0);
            }
        });
    }

    private void cargarProducto(final String codigo) {
        pb_carga.setProgress(75);
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
                            Log.e("asd", jsonArray.getJSONObject(i).getString("codigo"));
                            tv_nombre.setText(jsonArray.getJSONObject(i).getString("nombre"));
                            tv_descripcion.setText(jsonArray.getJSONObject(i).getString("descripcion"));
                            tv_modelo.setText(jsonArray.getJSONObject(i).getString("modelo"));
                            tv_precio.setText(jsonArray.getJSONObject(i).getString("precio"));
                            tv_stock.setText(jsonArray.getJSONObject(i).getString("stock"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pb_carga.setProgress(100);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Consulta.this);
                builder.setTitle("Error!: ");
                builder.setMessage("Error de conexión");
                builder.setPositiveButton("Aceptar", null);
                builder.show();
                pb_carga.setProgress(0);

            }
        });
    }

}
