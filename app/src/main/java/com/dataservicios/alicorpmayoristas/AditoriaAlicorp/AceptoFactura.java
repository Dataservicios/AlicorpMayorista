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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.alicorpmayoristas.AndroidCustomGalleryActivity;
import com.dataservicios.alicorpmayoristas.Model.Categoria;
import com.dataservicios.alicorpmayoristas.Model.PollDetail;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.util.AuditAlicorp;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Jaime on 28/10/2016.
 */

public class AceptoFactura extends Activity {
    private static final String LOG_TAG = VentanaVisible.class.getName();
    private Integer store_id, rout_id,audit_id, categoria_id , idAuditoria  ,sod_ventana_id, user_id ,poll_id,result;
    private Button btGuardar, bt_photo;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private String fechaRuta,category_name,comentario;
    private TextView tvCategoria;
    private TextView tvCuota;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Switch swAceptoFactura;
    private int is_sino=0 ;
    private String montoCuota;
    private PollDetail mPollDetail;
    private EditText  etComentario ;
    Categoria categoria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acepto_factura);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Facturas");

        db = new DatabaseHelper(getApplicationContext());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        poll_id = GlobalConstant.poll_id[4];



        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        rout_id =  bundle.getInt("rout_id");
        fechaRuta = bundle.getString("fechaRuta");
        audit_id = bundle.getInt("audit_id");
        categoria_id = bundle.getInt("categoria_id");
        montoCuota = bundle.getString("montoCuota");

        categoria = new Categoria();
        categoria = db.getCategoria(categoria_id);


        //bt_photo = (Button) findViewById(R.id.btPhoto);
        btGuardar = (Button) findViewById(R.id.btGuardar);
        tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        tvCuota = (TextView) findViewById(R.id.tvCuota);
        swAceptoFactura = (Switch) findViewById(R.id.swAceptoFactura);
        etComentario = (EditText) findViewById(R.id.etComentario);


        tvCategoria.setText("Categoría: " + categoria.getNombre());
        tvCuota.setText("Cuota: " + montoCuota);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);


//        bt_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takePhoto();
//            }
//        });
        swAceptoFactura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_sino= 1;
                } else {
                    is_sino= 0;
                }
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comentario = etComentario.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Presencia de productos");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        mPollDetail = new PollDetail();
                        mPollDetail.setPoll_id(poll_id);
                        mPollDetail.setStore_id(store_id);
                        mPollDetail.setSino(1);
                        mPollDetail.setOptions(0);
                        mPollDetail.setLimits(0);
                        mPollDetail.setMedia(0);
                        mPollDetail.setComment(1);
                        mPollDetail.setResult(is_sino);
                        mPollDetail.setLimite("0");
                        mPollDetail.setComentario(comentario);
                        mPollDetail.setAuditor(user_id);
                        mPollDetail.setProduct_id(0);
                        mPollDetail.setPublicity_id(0);
                        mPollDetail.setCategory_product_id(categoria_id);
                        mPollDetail.setCompany_id(GlobalConstant.company_id);
                        mPollDetail.setCommentOptions(0);
                        mPollDetail.setSelectdOptions("");
                        mPollDetail.setSelectedOtionsComment("");
                        mPollDetail.setPriority("0");

                        new loadPoll().execute(mPollDetail);


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

    class loadPoll extends AsyncTask<PollDetail, Integer , Boolean> {
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
        protected Boolean doInBackground(PollDetail... params) {
            // TODO Auto-generated method stub


            PollDetail mPD = params[0] ;

            if(!AuditAlicorp.insertPollDetail(mPD)) return false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){

                    // loadLoginActivity();
                if (is_sino == 1) {
                    Bundle argPDV = new Bundle();
                    argPDV.putInt("store_id", Integer.valueOf(store_id));
                    argPDV.putInt("rout_id", Integer.valueOf(rout_id));
                    argPDV.putInt("categoria_id", Integer.valueOf(categoria_id));
                    argPDV.putString("fechaRuta", fechaRuta);
                    argPDV.putInt("audit_id", audit_id);
                    argPDV.putString("montoCuota", montoCuota);

                    Intent intent = new Intent(MyActivity,FacturaRegistro.class);
                    intent.putExtras(argPDV);
                    startActivity(intent);
                    finish();
                } else {
                    categoria.setActive(0);
                    db.updateCategoria(categoria);
                    finish();
                }


            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }

    private void takePhoto() {

        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();
        bolsa.putString("store_id",String.valueOf(store_id));
        bolsa.putString("product_id",String.valueOf("0"));
        bolsa.putString("publicities_id","0");
        bolsa.putString("category_product_id", String.valueOf(categoria_id));
        bolsa.putString("poll_id",String.valueOf(poll_id));
        bolsa.putString("sod_ventana_id",String.valueOf(sod_ventana_id));
        bolsa.putString("company_id",String.valueOf(GlobalConstant.company_id));
        bolsa.putString("monto","20");
        bolsa.putString("razon_social","Pirulito");
        bolsa.putString("url_insert_image", GlobalConstant.dominio + "/insertImagesPublicitiesAlicorp");
        bolsa.putString("tipo", "1");
        i.putExtras(bolsa);
        startActivity(i);
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

//    @Override
//    public void onResume() {
//        super.onResume();
//
//
//    }
//
//    @Override
//    public void onRestart() {
//        super.onRestart();
//        //When BACK BUTTON is pressed, the activity on the stack is restarted
//        //Do what you want on the refresh procedure here
//        finish();
//        startActivity(getIntent());
//    }
}

