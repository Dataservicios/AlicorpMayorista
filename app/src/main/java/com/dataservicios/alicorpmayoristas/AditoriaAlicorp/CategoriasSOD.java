package com.dataservicios.alicorpmayoristas.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dataservicios.alicorpmayoristas.Model.SODVentanas;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.JSONParser;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

/**
 * Created by Jaime on 17/03/2016.
 */
public class CategoriasSOD extends Activity {
    private static final String LOG_TAG = CategoriasSOD.class.getSimpleName();
    private Activity MyActivity= this;

    private Button btCategoria1, btCategoria2,btCategoria3;

    private SessionManager session;

    private Button bt, btGuardar;

    private DatabaseHelper db;

    private ViewGroup linearLayout;


    private ProgressDialog pDialog;

    private int store_id, rout_id, company_id , audit_id,user_id , countProducts=0;
    private int  score = 0  ;
    private String fechaRuta ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Categorias");

//        btCategoria1 = (Button) findViewById(R.id.btCategoria1);
//        btCategoria2 = (Button) findViewById(R.id.btCategoria2);
//        btCategoria3 = (Button) findViewById(R.id.btCategoria3);

        linearLayout = (ViewGroup) findViewById(R.id.lyControles);
        btGuardar = (Button) findViewById(R.id.btGuardar);

        Bundle bundle = getIntent().getExtras();
        company_id =  GlobalConstant.company_id;
        store_id = bundle.getInt("store_id");
        rout_id = bundle.getInt("rout_id");
        fechaRuta = bundle.getString("fechaRuta");
        audit_id = bundle.getInt("audit_id");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        // id

        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        db = new DatabaseHelper(getApplicationContext());

        SODVentanas ct = new SODVentanas();

        List<SODVentanas> listaCategorias = new ArrayList<SODVentanas>();
        listaCategorias=db.getAllSODVentanas();

        listaCategorias.size();
        for (int i=0; i<listaCategorias.size(); i++) {
            //System.out.println(listaCategorias.get(i));
            final int sod_ventana_id = listaCategorias.get(i).getId();
            bt = new Button(MyActivity);
            LinearLayout ly = new LinearLayout(MyActivity);
            ly.setOrientation(LinearLayout.VERTICAL);
            ly.setId(i+'_');
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT
            );
            params.setMargins(0, 10, 0, 10);
            ly.setLayoutParams(params);
            bt.setBackgroundColor(getResources().getColor(R.color.color_base));
            bt.setTextColor(getResources().getColor(R.color.counter_text_bg));
            bt.setText(listaCategorias.get(i).getName().toUpperCase());


            int status = db.getSODVentanasStatus(sod_ventana_id);
            if(status==1) {
                Drawable img = MyActivity.getResources().getDrawable( R.drawable.ic_check_on);
                img.setBounds( 0, 0, 60, 60 );  // set the image size
                bt.setCompoundDrawables( img, null, null, null );
                bt.setBackgroundColor(getResources().getColor(R.color.color_bottom_buttom_pressed));
                bt.setTextColor(getResources().getColor(R.color.color_base));
                bt.setEnabled(false);
            }  else {
                Drawable img = MyActivity.getResources().getDrawable( R.drawable.ic_check_off);
                img.setBounds( 0, 0, 60, 60 );  // set the image size
                bt.setCompoundDrawables( img, null, null, null );
            }

            Drawable img = MyActivity.getResources().getDrawable( R.drawable.ic_check_off);
            img.setBounds( 0, 0, 60, 60 );  // set the image size
            bt.setCompoundDrawables( img, null, null, null );

            //bt.setBackground();
            bt.setId(sod_ventana_id);
            bt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), j  , Toast.LENGTH_LONG).show();
                    Button button1 = (Button) v;
                    String texto = button1.getText().toString();
                    //Toast toast=Toast.makeText(getActivity(), selected, Toast.LENGTH_SHORT);
                    Toast toast;
                    toast = Toast.makeText(MyActivity, texto + ":" + button1.getId(), Toast.LENGTH_LONG);
                    toast.show();
                    //int idBoton = Integer.valueOf(idAuditoria);
                    Intent intent;

                    Bundle argRuta = new Bundle();
                    argRuta.clear();
                    //argRuta.putInt("company_id", GlobalConstant.company_id);
                    argRuta.putInt("store_id",store_id);
                    argRuta.putInt("rout_id", rout_id );
                    argRuta.putString("fechaRuta", fechaRuta);
                    argRuta.putInt("audit_id", audit_id);
                    argRuta.putInt("sod_ventana_id",sod_ventana_id);
                    argRuta.putString("category_name",texto);

                    //db.deleteAllPresenseProduct();
                    //intent = new Intent(MyActivity, Ventanas.class);
                    intent = new Intent(MyActivity, VentanaExiste.class);
                    intent.putExtras(argRuta);
                    startActivity(intent);

                }
            });
            ly.addView(bt);
            linearLayout.addView(ly);
        }

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();



                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro desea finalizar: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        new auditPollProducts().execute();
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
//
//        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        Bundle argPDV = new Bundle();
////        argPDV.putInt("pdv_id", pdv_id );
////        argPDV.putInt("idRuta", idRuta );
////        argPDV.putString("fechaRuta",fechaRuta);
////        Intent a = new Intent(this,DetallePdv.class);
////        a.putExtras(argPDV);
////
////        startActivity(a);
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

    class auditPollProducts extends AsyncTask<Void, Integer , Boolean> {
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
            //cargaTipoPedido();

            InsertAuditProduct();

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){

                finish();
                hidepDialog();

            }
        }
    }


    private void InsertAuditProduct() {
        int success;
        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("poll_id", String.valueOf(store_id)));
            params.add(new BasicNameValuePair("company_id", String.valueOf(company_id)));
            params.add(new BasicNameValuePair("store_id", String.valueOf(store_id)));
            params.add(new BasicNameValuePair("idaudit", String.valueOf(audit_id)));
            params.add(new BasicNameValuePair("idroute", String.valueOf(rout_id)));
            params.add(new BasicNameValuePair("status", String.valueOf("0")));


            JSONParser jsonParser = new JSONParser();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/endPresenceProdAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            success = json.getInt("success");
            if (success == 1) {
                Log.d(LOG_TAG, json.getString("Ingresado correctamente"));
            }else{
                Log.d(LOG_TAG, json.getString("message"));
                // return json.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

