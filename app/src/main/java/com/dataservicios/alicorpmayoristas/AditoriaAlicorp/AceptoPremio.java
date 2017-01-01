package com.dataservicios.alicorpmayoristas.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.alicorpmayoristas.AndroidCustomGalleryActivity;
import com.dataservicios.alicorpmayoristas.Model.PollDetail;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.util.AuditAlicorp;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

import java.util.HashMap;

/**
 * Created by Jaime on 2/12/2016.
 */

public class AceptoPremio extends Activity {

    private static final String LOG_TAG = VentanaVisible.class.getName();
    private Integer store_id,  categoria_id , idAuditoria  ,sod_ventana_id, user_id ,poll_id,result;
    private Button btGuardar, bt_photo;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private String category_name,comentario;

    private ProgressDialog pDialog;
    private SessionManager session;
    private Switch swAceptoFactura;
    private int is_sino=0 ;

    private PollDetail mPollDetail;
    private EditText etComentario ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acepto_premio);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Facturas");

        db = new DatabaseHelper(getApplicationContext());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        poll_id = GlobalConstant.poll_id[8];
        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");




//        categoria = new Categoria();
//        categoria = db.getCategoria(categoria_id);


        //bt_photo = (Button) findViewById(R.id.btPhoto);
        btGuardar = (Button) findViewById(R.id.btGuardar);
        //tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        // tvCuota = (TextView) findViewById(R.id.tvCuota);
        swAceptoFactura = (Switch) findViewById(R.id.swAceptoFactura);
        etComentario = (EditText) findViewById(R.id.etComentario);
        bt_photo = (Button) findViewById(R.id.btPhoto);

        //tvCategoria.setText("Categoría: " + categoria.getNombre());
        // tvCuota.setText("Cuota: " + montoCuota);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        swAceptoFactura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_sino= 1;
                    bt_photo.setVisibility(View.VISIBLE);
                    bt_photo.setEnabled(true);
                } else {
                    is_sino= 0;
                    bt_photo.setVisibility(View.INVISIBLE);
                    bt_photo.setEnabled(false);
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
                        mPollDetail.setCategory_product_id(0);
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
                finish();

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
        bolsa.putString("category_product_id","0" );
        bolsa.putString("poll_id",String.valueOf(poll_id));
        bolsa.putString("sod_ventana_id",String.valueOf("0"));
        bolsa.putString("company_id",String.valueOf(GlobalConstant.company_id));
        bolsa.putString("monto","");
        bolsa.putString("razon_social","");
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


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
//            //Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar pongase en contácto con el administrador", Toast.LENGTH_LONG).show();
//            onBackPressed();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//    @Override
//    public void onBackPressed() {
//        Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar póngase en contácto con el administrador", Toast.LENGTH_LONG).show();
////        super.onBackPressed();
////        this.finish();
////
////        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
//    }

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

