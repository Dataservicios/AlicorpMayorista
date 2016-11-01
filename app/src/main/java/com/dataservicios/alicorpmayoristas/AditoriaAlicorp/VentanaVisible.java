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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * Created by Jaime on 27/05/2016.
 */
public class VentanaVisible extends Activity {

    private static final String LOG_TAG = VentanaVisible.class.getSimpleName();
    private Integer store_id, rout_id,audit_id, publicity_id , idAuditoria  ,sod_ventana_id, user_id ,poll_id, poll_id_2,result;
    private Button btGuardar, bt_photo;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private String fechaRuta,category_name;
    private TextView tvCategoria;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Switch swVisible;
    private int is_visible=0 ;

    private RadioGroup rgOpt1;

    private String opt1="";

    private RadioButton[] radioButton1Array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventana_visible);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Ventana visible");

        db = new DatabaseHelper(getApplicationContext());


        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        poll_id = 567;
        poll_id_2 = 568;


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

        bt_photo = (Button) findViewById(R.id.btPhoto);
        btGuardar = (Button) findViewById(R.id.btGuardar);
        tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        swVisible = (Switch) findViewById(R.id.swVisible);

        rgOpt1=(RadioGroup) findViewById(R.id.rgOpt);

        radioButton1Array = new RadioButton[] {
                (RadioButton) findViewById(R.id.rbA),
                (RadioButton) findViewById(R.id.rbB),
                (RadioButton) findViewById(R.id.rbC),
        };



        tvCategoria.setText("Categoría: " + category_name);


        swVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_visible= 1;
                } else {
                    is_visible= 0;
                }
            }
        });
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id1 = rgOpt1.getCheckedRadioButtonId();
                if (id1 == -1){
                    Toast.makeText(MyActivity,"Selecione una opción" , Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    for (int x = 0; x < radioButton1Array.length; x++) {
                        if(id1 ==  radioButton1Array[x].getId())  opt1 = poll_id_2.toString() + radioButton1Array[x].getTag();
                    }

                }



                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Ventana");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        JSONObject paramsData;
                        paramsData = new JSONObject();

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


            if(!InsertAuditPollsProduct(poll_id,store_id,audit_id,rout_id,user_id,publicity_id, is_visible)) return  false;
            if(!InsertAuditPollsProductOtions(poll_id_2,store_id,rout_id,audit_id,user_id, publicity_id,"0","0", opt1,"")) return  false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted
            if (result){

//                if(is_visible == 1) {
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
                    intent = new Intent(MyActivity, Ventanas.class);
                    //intent = new Intent(MyActivity, VentanaExiste.class);
                    intent.putExtras(argRuta);
                    startActivity(intent);
                    finish();

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

    private boolean InsertAuditPollsProductOtions(int poll_id, int store_id, int rout_id, int audit_id, int user_id, int publicity_id, String status , String result, String options, String comentario) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();
            params.put("poll_id", String.valueOf(poll_id));


            params.put("poll_id", String.valueOf( poll_id));
            params.put("store_id", String.valueOf(store_id));
            params.put("options", "1");
            params.put("opcion", options);
            params.put("result", result);
            params.put("company_id", String.valueOf(GlobalConstant.company_id));
            params.put("publicity_id", String.valueOf(publicity_id));



            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonInsertPollOptionPubli" ,"POST", params);
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "json/prueba.json" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            //success = json.getInt("success");
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

        return true;
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
}


