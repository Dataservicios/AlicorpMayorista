package com.dataservicios.alicorpmayoristas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.dataservicios.alicorpmayoristas.Model.User;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.util.Connectivity;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.JSONParser;
import com.dataservicios.alicorpmayoristas.util.SessionManager;


/**
 * Created by usuario on 05/11/2014.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    Button ingresar, btLlamar, btUbicar;
    EditText usuario,contrasena;
    private DatabaseHelper db;
    // Progress Dialog
    private ProgressDialog pDialog;
    Activity MyActivity = (Activity) this;
    // Session Manager Class
    SessionManager session;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ingresar = (Button) findViewById(R.id.btIngresar);
        usuario =   (EditText) findViewById(R.id.etUsuario);
        contrasena = (EditText) findViewById(R.id.etContrasena);

        contrasena.setText("123456");


        ingresar.setOnClickListener(this);
        // Session Manager
        session = new SessionManager(getApplicationContext());


        if(Connectivity.isConnected(MyActivity)) {
            if (Connectivity.isConnectedFast(MyActivity)) {
                Toast.makeText(MyActivity, "Conexion a internet rapida", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MyActivity, "Conexion a internet lenta", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MyActivity, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
        }

        db = new DatabaseHelper(getApplicationContext());
        if(db.getUserCount() > 0) {
            //User users = new User();
            List<User> usersList = db.getAllUser();
            if(usersList.size()>0) {
                User users = new User();
                users=usersList.get(0);
                usuario.setText(users.getEmail());
                //contrasena.setText(users.getPassword());
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btIngresar:

                if (usuario.getText().toString().trim().equals("") )
                {
                     Toast toast = Toast.makeText(this, "Ingrese un Usuario", Toast.LENGTH_SHORT);
                    toast.show();
                     usuario.requestFocus();
                }else if (contrasena.getText().toString().trim().equals("")) {
                    Toast toast = Toast.makeText(this, "Ingrese una Contraseña ", Toast.LENGTH_SHORT);
                    toast.show();
                    contrasena.requestFocus();
                }else {
                            new AttemptLogin().execute();

                }
                break;

        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Iniciando Sesión...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Comprobando si es exito
            int success;
            String username = usuario.getText().toString();
            String password = contrasena.getText().toString();
            String userFullName ="";
            int id_user ;
            try {
                // Construyendo los parametros
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/loginUser" ,"POST", params);
                JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/loginMovil" ,"POST", params);

                if (json != null) {
                    // check your log for json response
                    Log.d("Login attempt", json.toString());

                    // json success, tag que retorna el json
                    success = json.getInt("success");
                    id_user = json.getInt("id");
                    userFullName= json.getString("fullname");
                    if (success == 1) {
                        Log.d("Login Successful!", json.toString());
                        db.deleteAllUser();
                        User users = new User();
                        users.setId(id_user);

                        users.setEmail(usuario.getText().toString());
                        users.setName(userFullName);
                        users.setPassword(contrasena.getText().toString());
                        db.createUser(users);
                        // Creating user login session
                        // For testing i am stroing name, email as follow
                        // Use user real data
                        session.createLoginSession(users.getName().toString(), users.getEmail(), String.valueOf(id_user));
                        Intent i = new Intent(LoginActivity.this, PanelAdmin.class);
                        //Enviando los datos usando Bundle a otro activity
                        Bundle bolsa = new Bundle();
                        bolsa.putString("NOMBRE", username);
                        i.putExtras(bolsa);
                        finish();
                        startActivity(i);

                        return json.getString("message");
                    }else{
                        Log.d("Login Failure!", json.getString("message"));

                        return json.getString("message");
                    }
                }

            } catch (JSONException e) {
                //Toast.makeText(LoginActivity.this, "No se pudo conectar", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

            if (file_url != null){
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(LoginActivity.this, "No se pudo obtener información del servidor", Toast.LENGTH_LONG).show();
            }

        }

    }
}
