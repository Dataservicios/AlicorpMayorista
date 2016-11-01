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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.alicorpmayoristas.Model.Categoria;
import com.dataservicios.alicorpmayoristas.Model.Cuota;
import com.dataservicios.alicorpmayoristas.Model.PollDetail;
import com.dataservicios.alicorpmayoristas.Model.Product;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.util.AuditAlicorp;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

import java.util.HashMap;

/**
 * Created by Jaime on 27/10/2016.
 */

public class CumpleCuota extends Activity {

    private Activity MyActivity = this ;
    private static final String LOG_TAG = ExisteProducto.class.getSimpleName();
    private SessionManager session;
    private Switch swExhibidorExiste ;
    private Button bt_guardar;
    private TextView tvCuota,tvCatgoria;
    private String tipo,cadenaruc, fechaRuta;
    private Integer user_id,store_id,rout_id,audit_id, categoria_id, poll_id, company_id ;
    private String montoCuota;
    int  is_sino=0 ;
    private DatabaseHelper db;
    private ProgressDialog pDialog;
    private PollDetail mPollDetail;


    Categoria categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cumple_cuota);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Cumple Cuota");

        swExhibidorExiste = (Switch) findViewById(R.id.swExhibidorExiste);


        // tv_Pregunta = (TextView) findViewById(R.id.tvPregunta);
        bt_guardar = (Button) findViewById(R.id.btGuardar);
        tvCuota = (TextView) findViewById(R.id.tvCuota);
        tvCatgoria = (TextView) findViewById(R.id.tvCategoria);

        Bundle bundle = getIntent().getExtras();

        company_id = GlobalConstant.company_id;
        store_id = bundle.getInt("store_id");
        rout_id = bundle.getInt("rout_id");
        categoria_id = bundle.getInt("categoria_id");
        audit_id = bundle.getInt("audit_id");
        fechaRuta = bundle.getString("fechaRuta");

        poll_id = GlobalConstant.poll_id[3]; // 0 "Existe Ventana?"

        db = new DatabaseHelper(getApplicationContext());
        categoria = new Categoria();
        categoria = db.getCategoria(categoria_id);

        tvCatgoria.setText("Categoría: " + categoria.getNombre());

        //poll_id = 72 , solo para exhibiciones de bayer, directo de la base de datos

        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;


        new loadCuota().execute();

        swExhibidorExiste.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_sino = 1;

                } else {
                    is_sino = 0;


                }
            }
        });




        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mPollDetail = new PollDetail();
                        mPollDetail.setPoll_id(poll_id);
                        mPollDetail.setStore_id(store_id);
                        mPollDetail.setSino(1);
                        mPollDetail.setOptions(0);
                        mPollDetail.setLimits(0);
                        mPollDetail.setMedia(0);
                        mPollDetail.setComment(0);
                        mPollDetail.setResult(is_sino);
                        mPollDetail.setLimite(0);
                        mPollDetail.setComentario("");
                        mPollDetail.setAuditor(user_id);
                        mPollDetail.setProduct_id(0);
                        mPollDetail.setCategory_product_id(categoria_id);
                        mPollDetail.setPublicity_id(0);
                        mPollDetail.setCompany_id(GlobalConstant.company_id);
                        mPollDetail.setCommentOptions(0);
                        mPollDetail.setSelectdOptions("");
                        mPollDetail.setSelectedOtionsComment("");
                        mPollDetail.setPriority("0");

                        new loadPoll().execute(mPollDetail);

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
                if (is_sino == 1) {

                    Bundle argPDV = new Bundle();
                    argPDV.putInt("store_id", Integer.valueOf(store_id));
                    argPDV.putInt("rout_id", Integer.valueOf(rout_id));
                    argPDV.putInt("categoria_id", Integer.valueOf(categoria_id));
                    argPDV.putString("fechaRuta", fechaRuta);
                    argPDV.putInt("audit_id", audit_id);
                    argPDV.putString("montoCuota", montoCuota);

                    Intent intent = new Intent(MyActivity,AceptoFactura.class);
                    intent.putExtras(argPDV);
                    startActivity(intent);
                    finish();

                } else if(is_sino == 0) {
                    // loadLoginActivity();
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



    class loadCuota extends AsyncTask<Void, Integer , Cuota> {
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
        protected Cuota doInBackground(Void... params) {
            // TODO Auto-generated method stub


            Cuota cuota = new Cuota();

            cuota = AuditAlicorp.getCuota(store_id) ;


            return cuota;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Cuota cuota) {
            // dismiss the dialog once product deleted

            if (cuota.getCuota() != null ){
                // loadLoginActivity();

                String foo = cuota.getCuota();
                String[] split = foo.split("\\|");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                      String[]  strCategoriaCuota =   split[i].split("\\:");
                      int  intCategoria_id_ =   Integer.valueOf(strCategoriaCuota[0]);
                      String  strCuota =   strCategoriaCuota[1];
                    if(intCategoria_id_ == categoria_id){
                        tvCuota.setText("Cuota: " + strCuota);
                        montoCuota = strCuota;
                    }

                }
                //String joined = sb.toString();
                //tvCuota.setText(cuota.getCuota());
                //tvCuota.setText(sb);


            } else {
                Toast.makeText(MyActivity , "No se pudo obtener la cuota, intentelo nuevamente", Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
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
