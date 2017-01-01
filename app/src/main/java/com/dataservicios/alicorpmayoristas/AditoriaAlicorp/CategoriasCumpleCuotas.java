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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.alicorpmayoristas.Model.Categoria;
import com.dataservicios.alicorpmayoristas.Model.Product;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.adapter.CategoriaAdapter;
import com.dataservicios.alicorpmayoristas.app.AppController;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.JSONParserX;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jaime on 27/10/2016.
 */

public class CategoriasCumpleCuotas extends Activity {

    private Activity MyActivity = this ;
    private static final String LOG_TAG = PresenciaProducto.class.getSimpleName();
    private SessionManager session;


    private ListView listView;
    private CategoriaAdapter adapter;
    private DatabaseHelper db;

    private ProgressDialog pDialog;


    private List<Categoria> categoriaList = new ArrayList<Categoria>();
    //private List<PresencePublicity> presencePubli = new ArrayList<PresencePublicity>();

    private int store_id, rout_id, compay_id , audit_id,user_id ,  categoria_id;
    private int  score = 0  ;
    private String fechaRuta,typeBodega ;
    private Button bt_guardar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.categoria_cumple_cuota);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Cumple Montos");

        db = new DatabaseHelper(getApplicationContext());

        bt_guardar = (Button) findViewById(R.id.btGuardar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        compay_id =  GlobalConstant.company_id;
        store_id = bundle.getInt("store_id");
        rout_id = bundle.getInt("rout_id");
        fechaRuta = bundle.getString("fechaRuta");
        audit_id = bundle.getInt("audit_id");
        typeBodega = bundle.getString("typeBodega");


//        Audit au = new Audit();
//        au = db.getAudit(audit_id);
//        getActionBar().setTitle(au.getName());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;





        listView = (ListView) findViewById(R.id.listCategoria);

        adapter = new CategoriaAdapter(this, categoriaList);
        listView.setAdapter(adapter);
        Log.d(LOG_TAG, String.valueOf(db.getAllPublicity()));

        List<Categoria> publi = db.getAllCategorias();

        Log.d(LOG_TAG, String.valueOf(publi));
        for(int i = 0; i < publi.size(); i++){

            Categoria m = new Categoria();
            m.setId(publi.get(i).getId());
            m.setNombre(publi.get(i).getNombre());
            m.setActive(publi.get(i).getActive());
            categoriaList.add(m);
        }


        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                categoria_id = Integer.valueOf(((TextView) view.findViewById(R.id.tvId)).getText().toString());
                Categoria p = new Categoria();
                p=db.getCategoria(categoria_id);
                Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(categoria_id), Toast.LENGTH_SHORT);
                toast.show();

                Bundle argPDV = new Bundle();
                argPDV.putInt("store_id", Integer.valueOf(store_id));
                argPDV.putInt("rout_id", Integer.valueOf(rout_id));
                argPDV.putInt("categoria_id", Integer.valueOf(categoria_id));
                argPDV.putString("fechaRuta", fechaRuta);
                argPDV.putInt("audit_id", audit_id);
                Intent intent = new Intent(MyActivity,CumpleCuota.class);
                intent.putExtras(argPDV);
                startActivity(intent);




            }
        });

        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int totalItems=listView.getAdapter().getCount();
                String[] items = new String[totalItems];
                for(int i = 0; i < totalItems; i++){
                    items[i] = listView.getAdapter().getItem(i).toString();
                    Categoria p =  new Categoria();
                    p = (Categoria) adapter.getItem(i);

                    if(p.getActive()==1) {

                        String nombre = p.getNombre();
                        Toast.makeText(MyActivity,"Está pendiente para auditar " + nombre , Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar la lista de publicidades: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {


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


            if(!closeAudit(store_id,audit_id, rout_id)) return false ;



            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){

                db.updatePublicityDesactive();

                Bundle argPDV = new Bundle();

                argPDV.putInt("store_id", Integer.valueOf(store_id));
                argPDV.putInt("rout_id", Integer.valueOf(rout_id));
                argPDV.putInt("categoria_id", Integer.valueOf(categoria_id));
                argPDV.putString("fechaRuta", fechaRuta);
                argPDV.putInt("audit_id", audit_id);

                Intent intent = new Intent(MyActivity,FirmaCargo.class);
                intent.putExtras(argPDV);
                startActivity(intent);

                finish();


            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }


    private Boolean closeAudit(int store_id, int audit_id, int rout_id  ) {
        int success;
        try {


            HashMap<String, String> params = new HashMap<>();

            params.put("store_id", String.valueOf(store_id));
            params.put("audit_id", String.valueOf(audit_id));
            params.put("company_id", String.valueOf(GlobalConstant.company_id));
            params.put("rout_id", String.valueOf(rout_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/closeAudit" ,"POST", params);
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
