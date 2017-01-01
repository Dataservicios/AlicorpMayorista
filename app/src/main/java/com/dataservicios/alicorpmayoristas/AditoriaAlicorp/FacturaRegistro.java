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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.alicorpmayoristas.AndroidCustomGalleryActivity;
import com.dataservicios.alicorpmayoristas.Model.Categoria;
import com.dataservicios.alicorpmayoristas.Model.Media;
import com.dataservicios.alicorpmayoristas.Model.PollDetail;
import com.dataservicios.alicorpmayoristas.Model.RegInvoice;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.Repositories.MediaRepo;
import com.dataservicios.alicorpmayoristas.Repositories.RegInvoiceRepo;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.adapter.MediaAdapter;
import com.dataservicios.alicorpmayoristas.adapter.RegFacturaAdapter;
import com.dataservicios.alicorpmayoristas.util.AuditAlicorp;
import com.dataservicios.alicorpmayoristas.util.BitmapLoader;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jaime on 27/11/2016.
 */

public class FacturaRegistro extends Activity {
    private static final String LOG_TAG = VentanaVisible.class.getName();
    private Integer store_id, rout_id,audit_id, categoria_id , idAuditoria  ,sod_ventana_id, user_id ,poll_id,result;
    private Button btFinalizar, btNuevo;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private String fechaRuta,category_name;

    private ListView listView;
    private RegFacturaAdapter adapter;
    private RegInvoiceRepo mr ;
    private List<RegInvoice> regInvoiceList = new ArrayList<RegInvoice>();


    private ProgressDialog pDialog;
    private SessionManager session;

    private int is_sino=0 ;
    private String montoCuota;
    private PollDetail mPollDetail;
    Categoria categoria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factura_registro);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setTitle("Facturas");

        db = new DatabaseHelper(getApplicationContext());

        mr = new RegInvoiceRepo(MyActivity);

        int total_products = mr.getAllRegInvoice().size();
        //tv_contador.setText(String.valueOf(total_products));

        listView = (ListView) findViewById(R.id.listProducts);
        regInvoiceList =  mr.getAllRegInvoice();
        // adapter = new PublicityAdapter(this, db.getAllPublicity());
        adapter = new RegFacturaAdapter(MyActivity,  regInvoiceList);
        listView.setAdapter(adapter);
        Log.d(LOG_TAG, String.valueOf(mr.getAllRegInvoice()));
        adapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // selected item
                String product_id = ((TextView) view.findViewById(R.id.tvId)).getText().toString();
                Toast toast = Toast.makeText(getApplicationContext(), product_id, Toast.LENGTH_SHORT);
                toast.show();


            }

        });

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
        btFinalizar = (Button) findViewById(R.id.btFinalizar);
        btNuevo = (Button) findViewById(R.id.btNuevo);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);


//        bt_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takePhoto();
//            }
//        });


        btNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapLoader.deleteFilesDirectory(BitmapLoader.getAlbumDir(MyActivity).getAbsolutePath());

                Bundle argPDV = new Bundle();
                argPDV.putInt("store_id", Integer.valueOf(store_id));
                argPDV.putInt("rout_id", Integer.valueOf(rout_id));
                argPDV.putInt("categoria_id", Integer.valueOf(categoria_id));
                argPDV.putString("fechaRuta", fechaRuta);
                argPDV.putInt("audit_id", audit_id);
                argPDV.putString("montoCuota", montoCuota);

                Intent intent = new Intent(MyActivity,FacturaNueva.class);
                intent.putExtras(argPDV);
                startActivity(intent);
                //finish();
            }
        });

        btFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Presencia de productos");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        categoria.setActive(0);
                        db.updateCategoria(categoria);

                        RegInvoiceRepo mr = new RegInvoiceRepo(MyActivity);
                        mr.deleteAll();

                        finish();


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

//    class loadPoll extends AsyncTask<PollDetail, Integer , Boolean> {
//        /**
//         * Antes de comenzar en el hilo determinado, Mostrar progresión
//         * */
//        boolean failure = false;
//        @Override
//        protected void onPreExecute() {
//            //tvCargando.setText("Cargando Product...");
//            pDialog.show();
//            super.onPreExecute();
//        }
//        @Override
//        protected Boolean doInBackground(PollDetail... params) {
//            // TODO Auto-generated method stub
//
//
//            PollDetail mPD = params[0] ;
//
//            if(!AuditAlicorp.insertPollDetail(mPD)) return false;
//
//            return true;
//        }
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(Boolean result) {
//            // dismiss the dialog once product deleted
//
//            if (result){
//
//                // loadLoginActivity();
//                if (is_sino == 1) {
//
//                } else {
//                    categoria.setActive(0);
//                    db.updateCategoria(categoria);
//                    finish();
//                }
//
//
//            } else {
//                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
//            }
//            hidepDialog();
//        }
//    }



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

