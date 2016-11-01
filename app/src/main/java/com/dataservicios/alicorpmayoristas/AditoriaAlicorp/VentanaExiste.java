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

import org.json.JSONObject;

import java.util.HashMap;

import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.JSONParserX;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

/**
 * Created by Jaime on 29/04/2016.
 */
public class VentanaExiste extends Activity {
    private static final String LOG_TAG = CategoriasSOD.class.getSimpleName();
    private Integer store_id, rout_id,audit_id, publicity_id   ,sod_ventana_id, user_id ,poll_id,result;
    private Button btGuardar;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private String fechaRuta,category_name;
    private TextView tvCategoria;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Switch swVentanaExiste;
    private int is_ventana_existe=0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventana_existe);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("SOD Ventanas");

        db = new DatabaseHelper(getApplicationContext());


        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        poll_id = 566;

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        rout_id =  bundle.getInt("rout_id");
        fechaRuta = bundle.getString("fechaRuta");
        sod_ventana_id = bundle.getInt("sod_ventana_id");
        audit_id = bundle.getInt("audit_id");
        category_name = bundle.getString("category_name");
        publicity_id = sod_ventana_id;



        btGuardar = (Button) findViewById(R.id.btGuardar);
        tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        swVentanaExiste = (Switch) findViewById(R.id.swVentanaExiste);


        tvCategoria.setText("Categoría: " + category_name);




        swVentanaExiste.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_ventana_existe = 1;
                } else {
                    is_ventana_existe = 0;
                }
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Ventana");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast toast;
//                        toast = Toast.makeText(MyActivity, "Ha guardó correctamente", Toast.LENGTH_LONG);
//                        toast.show();

                        JSONObject paramsData;
                        paramsData = new JSONObject();


                            new loadPoll().execute();


                            db.updateSODVentanasStatus(sod_ventana_id, 1);

                        dialog.dismiss();

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


            if(!InsertAuditPollsProduct(poll_id,store_id,audit_id,rout_id,user_id,publicity_id, is_ventana_existe)) return false;


            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted
            if (result){

                if(is_ventana_existe == 1) {
                    Intent intent;
                    Bundle argRuta = new Bundle();
                    argRuta.clear();
                    //argRuta.putInt("company_id", GlobalConstant.company_id);
                    argRuta.putInt("store_id",store_id);
                    argRuta.putInt("rout_id", rout_id);
                    argRuta.putString("fechaRuta", fechaRuta);
                    argRuta.putInt("audit_id", audit_id);
                    argRuta.putInt("sod_ventana_id", sod_ventana_id);
                    argRuta.putString("category_name",category_name);

                    //db.deleteAllPresenseProduct();
                   // intent = new Intent(MyActivity, Ventanas.class);
                    intent = new Intent(MyActivity, VentanaVisible.class);
                    intent.putExtras(argRuta);
                    startActivity(intent);
                    finish();
                } else if(is_ventana_existe == 0) {

                    db.updateSODVentanasStatus(sod_ventana_id, 1);

                    finish();
                }


            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
            }

            hidepDialog();
        }
    }


    private boolean InsertAuditPollsProduct(int poll_id, int store_id, int audit_id,int rout_id, int user_id,int publicity_id , int result) {
        int success;
        try {
            HashMap<String, String> params = new HashMap<>();

            params.put("poll_id", String.valueOf(poll_id));
            params.put("store_id", String.valueOf(store_id));
            params.put("result", String.valueOf(result));
            params.put("audit_id", String.valueOf(audit_id));
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
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                   // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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



//    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
//    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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

}
