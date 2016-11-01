package com.dataservicios.alicorpmayoristas.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import com.dataservicios.alicorpmayoristas.MainActivity;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.JSONParserX;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

/**
 * Created by Jaime on 16/08/2016.
 */
public class ClientePerfecto extends Activity {
    private Activity MyActivity = this ;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SessionManager session;

    private Switch swClientePerfecto ;
    private Button bt_guardar;
    private EditText etComentario;
    private TextView tv_Pregunta;
    private LinearLayout lyNoClientPerfet ;

    private String tipo,cadenaruc, fechaRuta, comentario="", type, region;

    private Integer user_id, company_id,store_id,rout_id,audit_id, product_id, poll_id, poll_id2;

    int  is_clientPerfect=0 ;


    private DatabaseHelper db;

    private ProgressDialog pDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_perfecto);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Cliente Perfecto");

        swClientePerfecto = (Switch) findViewById(R.id.swClientePerfecto);

        lyNoClientPerfet = (LinearLayout) findViewById(R.id.lyNoClientPerfet);





        tv_Pregunta = (TextView) findViewById(R.id.tvPregunta);
        bt_guardar = (Button) findViewById(R.id.btGuardar);

        //etComentario = (EditText) findViewById(R.id.etComentario);
        etComentario = (EditText) findViewById(R.id.etComentario);





        Bundle bundle = getIntent().getExtras();
        company_id = GlobalConstant.company_id;
        store_id = bundle.getInt("store_id");
        rout_id = bundle.getInt("rout_id");
        region = bundle.getString("region");
        fechaRuta = bundle.getString("fechaRuta");
        audit_id = bundle.getInt("audit_id");

        type = bundle.getString("type");



        //audit_id = 4; //Inicio de auditoría Alicorp
        poll_id = 570;
        poll_id2 = 571;

        //poll_id = 72 , solo para exhibiciones de bayer, directo de la base de datos

        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;



        swClientePerfecto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_clientPerfect = 1;

                    lyNoClientPerfet.setEnabled(false);
                    lyNoClientPerfet.setVisibility(View.VISIBLE);
                    etComentario.setText("");


                } else {
                    is_clientPerfect = 0;

                    lyNoClientPerfet.setEnabled(true);
                    lyNoClientPerfet.setVisibility(View.INVISIBLE);
                    etComentario.setText("");
                }
            }
        });




        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!swClientePerfecto.isChecked()){
//

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comentario = String.valueOf(etComentario.getText()) ;
                        new loadPoll().execute();
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

            if(is_clientPerfect==1) {

                if(!InsertAuditPollsProduct(store_id,poll_id,0,is_clientPerfect,0,"")) return false ;
                if(!InsertAuditPollsProduct(store_id,poll_id2,0,is_clientPerfect,0,comentario)) return false ;

            } else if(is_clientPerfect==0){
                if(!InsertAuditPollsProduct(store_id,poll_id,1,is_clientPerfect,0,"")) return false ;
            }


            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                // loadLoginActivity();
                if(is_clientPerfect==1) {
                        Bundle argRuta = new Bundle();
                        argRuta.clear();
                        argRuta.putInt("store_id", store_id);
                        argRuta.putString("fechaRuta", fechaRuta);
                        argRuta.putInt("audit_id", audit_id);
                        argRuta.putInt("rout_id", rout_id);
                        argRuta.putString("type", type);
                        argRuta.putString("region", region);
                        Intent intent;
                        //intent = new Intent(MyActivity, Product.class);
                        //intent = new Intent(MyActivity, TipoDex.class);
                        intent = new Intent(MyActivity, ClientePerfectoPremiado.class);
                        intent.putExtras(argRuta);
                        startActivity(intent);
                        finish();
                } else if(is_clientPerfect==0){
//                    Bundle argRuta = new Bundle();
//                    argRuta.clear();
//                    argRuta.putInt("store_id", store_id);
//                    argRuta.putInt("rout_id", rout_id);
//                    Intent intent;
//                    intent = new Intent(MyActivity, Ubicacion.class);
//                    intent.putExtras(argRuta);
//                    startActivity(intent);
                    finish();
                }



            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }



    private Boolean InsertAuditPollsProduct(int store_id , int poll_id, int status , int result, int publicity_id, String comentario) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();


            params.put("store_id", String.valueOf(store_id));
            params.put("poll_id", String.valueOf(poll_id));
            params.put("publicity_id", String.valueOf(publicity_id));
            params.put("company_id", String.valueOf(GlobalConstant.company_id));
            params.put("rout_id", String.valueOf(rout_id));
            params.put("audit_id", String.valueOf(audit_id));
            params.put("user_id", String.valueOf(user_id));
            params.put("status", String.valueOf(status));
            params.put("result", String.valueOf(result));
            params.put("comentario", String.valueOf(comentario));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonInsertPollsAlicorp" ,"POST", params);
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
                    // Log.d(LOG_TAG, json.getString("Ingresado correctamente"));
                    Log.d(LOG_TAG, "Ingresado correctamente");

                }else{
                    //  Log.d(LOG_TAG, json.getString("message"));
                    // return json.getString("message");
                    Log.d(LOG_TAG, "Error al ingresar registro");
                   // return false;
                }
            }

        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            e.printStackTrace();
            return  false;
        }

        return  true;
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

    /**
     * Desactiva o activa todos los elementos del radiobutton
     * @param radioButtonArray Array de RadioButton
     * @param checked parametro  en cheked
     */
    private void clearRadioButtonCheck(RadioButton[] radioButtonArray, boolean checked){
        for (int x = 0; x < radioButtonArray.length; x++) {
            radioButtonArray[x].setChecked(checked);
        }
    }

    /**
     * Metodo evalua si esta marcado alguna opción de un radio button
     * @param radioButtonArray
     * @return
     */
    private boolean evaluateOptionRadioButton(RadioButton[] radioButtonArray){
        boolean status= true;
        for (int x = 0; x < radioButtonArray.length; x++) {
            if(!radioButtonArray[x].isChecked())  {
                status = false ;
                break;
            }
        }
        return status;
    }


    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
