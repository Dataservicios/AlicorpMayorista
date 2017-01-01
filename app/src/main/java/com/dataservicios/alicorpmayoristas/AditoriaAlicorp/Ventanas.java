package com.dataservicios.alicorpmayoristas.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.dataservicios.alicorpmayoristas.AndroidCustomGalleryActivity;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.app.AppController;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.JSONParserX;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

/**
 * Created by Jaime on 17/03/2016.
 */
public class Ventanas extends Activity {
    private static final String LOG_TAG = VentanaVisible.class.getName();
    private Integer store_id, rout_id,audit_id, publicity_id , idAuditoria  ,sod_ventana_id, user_id ,poll_id,result;
    private Button btGuardar, bt_photo;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private String fechaRuta,category_name;
    private TextView tvCategoria;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Switch swPrecioVisible;
    private int is_precio_visible=0 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventanas);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("SOD Ventanas");

        db = new DatabaseHelper(getApplicationContext());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        poll_id = 565;



        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        rout_id =  bundle.getInt("rout_id");
        fechaRuta = bundle.getString("fechaRuta");
        sod_ventana_id = bundle.getInt("sod_ventana_id");
        audit_id = bundle.getInt("audit_id");
        category_name = bundle.getString("category_name");
        publicity_id = sod_ventana_id;


        bt_photo = (Button) findViewById(R.id.btPhoto);
        btGuardar = (Button) findViewById(R.id.btGuardar);
        tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        swPrecioVisible = (Switch) findViewById(R.id.swPrecioVisible);


        tvCategoria.setText("Categoría: " + category_name);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);


        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        swPrecioVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_precio_visible= 1;
                } else {
                    is_precio_visible= 0;
                }
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//               if (db.getCountPresenseProduct() == 0 ) {
//                   Toast toast;
//                   toast = Toast.makeText(MyActivity, "No se puede guardar la lista vacía", Toast.LENGTH_LONG);
//                   toast.show();
//                   return;
//               }
//
//                db.getAllPresenceProduct();
//





                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Presencia de productos");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {



                        db.updateSODVentanasStatus(sod_ventana_id,1);
                        new loadPoll().execute();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);

            }
        });

    }



    private void takePhoto() {

        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();

        bolsa.putString("store_id", String.valueOf(store_id));
        bolsa.putString("product_id", String.valueOf("0"));
        bolsa.putString("publicities_id", String.valueOf(publicity_id));
        bolsa.putString("poll_id", String.valueOf("0"));
        bolsa.putString("sod_ventana_id", String.valueOf(sod_ventana_id));
        bolsa.putString("company_id", String.valueOf(GlobalConstant.company_id));
        bolsa.putString("monto","");
        bolsa.putString("razon_social","");
        bolsa.putString("url_insert_image", GlobalConstant.dominio + "/insertImagesPublicitiesAlicorp");
        bolsa.putString("tipo", "1");


        i.putExtras(bolsa);
        startActivity(i);

    }


    class loadPoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub


            if(!insertPoolAudit()) return  false;
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted
            if (result){

                finish();

            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
            }

            hidepDialog();
        }
    }

    private boolean insertPoolAudit() {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("store_id", String.valueOf(store_id) );
            params.put("poll_id", String.valueOf(poll_id));
            params.put("result", String.valueOf(is_precio_visible));
            params.put("audit_id", String.valueOf( audit_id));
            params.put("company_id", String.valueOf(GlobalConstant.company_id));
            params.put("rout_id", String.valueOf(rout_id));
            params.put("user_id", String.valueOf(user_id));
            params.put("publicity_id", String.valueOf(publicity_id));



            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/saveSODBodegaAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return  true;
    }

    private void insertAudit(JSONObject paramsData) {
        //showpDialog();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/saveSODBodegaAlicorp" ,paramsData,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            //compay_id =response.getInt("company");
                            if (success == 1) {

                                Toast toast;
                                toast = Toast.makeText(MyActivity, "Se guardó el registro correctamente ", Toast.LENGTH_LONG);
                                toast.show();
                                // onBackPressed();
//                                Bundle argRuta = new Bundle();
//                                argRuta.clear();
//                                argRuta.putInt("company_id",compay_id);
//                                argRuta.putInt("pdv_id",pdv_id);
//                                argRuta.putInt("idRuta", idRuta );
//                                argRuta.putInt("idAuditoria",idAuditoria);
//
//                                Intent intent;
//                                intent = new Intent(MyActivity,informacionCuatro.class);
//                                intent.putExtras(argRuta);
//                                startActivity(intent);
                                finish();
                                hidepDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
//                this.finish();
//                Intent a = new Intent(this,PanelAdmin.class);
//                //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(a);
//                overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            //Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar pongase en contácto con el administrador", Toast.LENGTH_LONG).show();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar póngase en contácto con el administrador", Toast.LENGTH_LONG).show();
//        super.onBackPressed();
//        this.finish();
//
//        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }
}
