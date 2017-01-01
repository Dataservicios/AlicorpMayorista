package com.dataservicios.alicorpmayoristas.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.dataservicios.alicorpmayoristas.Services.UploadService;
import com.dataservicios.alicorpmayoristas.util.AuditAlicorp;
import com.dataservicios.alicorpmayoristas.util.BitmapLoader;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Jaime on 27/11/2016.
 */

public class FacturaNueva extends Activity {
    private static final String LOG_TAG = VentanaVisible.class.getName();
    private static final int TAKE_PICTURE = 1;
    private String mCurrentPhotoPath;
    File[] listFile;

    private Integer store_id, rout_id,audit_id, categoria_id , idAuditoria  ,sod_ventana_id, user_id ,poll_id,result,publicities_id,product_id;
    private Button btGuardar, bt_photo;
    private DatabaseHelper db ;
    private Activity MyActivity = this ;
    private ImageView thumbnail;
    private String fechaRuta,category_name, imageName, montoFactura, razonSocial;
    private EditText etRazonSocial,etMontoFactura ;
    ArrayList<String> names_file = new ArrayList<String>();

    private ProgressDialog pDialog;
    private SessionManager session;

    private int is_sino=0 ;
    private String montoCuota;
    private PollDetail mPollDetail;
    Categoria categoria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factura_nueva);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setTitle("Facturas");

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


        bt_photo = (Button) findViewById(R.id.btPhoto);
        btGuardar = (Button) findViewById(R.id.btGuardar);
        etRazonSocial = (EditText) findViewById(R.id.etRazonSocial);
        etMontoFactura = (EditText) findViewById(R.id.etMontoFactura);
        thumbnail = (ImageView)  findViewById(R.id.thumbnail);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        getFromSdcard();

        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent with ACTION_IMAGE_CAPTURE action
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Bundle bundle = getIntent().getExtras();
                String idPDV = bundle.getString("idPDV");

                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = String.format("%06d", Integer.parseInt(store_id.toString()))+ "_" + GlobalConstant.company_id + GlobalConstant.JPEG_FILE_PREFIX + timeStamp;

                File albumF = BitmapLoader.getAlbumDir(MyActivity); // getAlbumDir();
                // to save picture remove comment
                File file = new File(albumF,imageFileName+GlobalConstant.JPEG_FILE_SUFFIX);

                Uri photoPath = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);

                mCurrentPhotoPath = BitmapLoader.getAlbumDir(MyActivity).getAbsolutePath();

                // start camera activity
                startActivityForResult(intent, TAKE_PICTURE);

                //takePhoto();
            }
        });


        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageName = thumbnail.getTag().toString().trim();
                montoFactura = etMontoFactura.getText().toString().trim();
                razonSocial = etRazonSocial.getText().toString().trim();
                if(imageName.equals("")) {
                    Toast.makeText(MyActivity,"Debe tomar foto de una factura",Toast.LENGTH_LONG).show();
                    return;
                }
                if(razonSocial.equals("")) {
                    Toast.makeText(MyActivity,"Debe ingresar la Razón Social",Toast.LENGTH_LONG).show();
                    return;
                }
                if(montoFactura.equals("")) {
                    Toast.makeText(MyActivity,"Debe ingresar el monto de la factura",Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Presencia de productos");
                builder.setMessage("Está seguro de guardar todas los datos: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        try {



                            BitmapLoader.copyFile(BitmapLoader.getAlbumDir(MyActivity) + "/" + thumbnail.getTag().toString() , BitmapLoader.getAlbumDirTemp(MyActivity) + "/" + thumbnail.getTag().toString() );
                            BitmapLoader.copyFile(BitmapLoader.getAlbumDir(MyActivity) + "/" + thumbnail.getTag().toString() , BitmapLoader.getAlbumDirBackup(MyActivity) + "/" + thumbnail.getTag().toString()  );

                            names_file.clear();
                            names_file.add(thumbnail.getTag().toString());

                            BitmapLoader.deleteFilesDirectory(BitmapLoader.getAlbumDir(MyActivity).getAbsolutePath());

                            RegInvoiceRepo mr = new RegInvoiceRepo(MyActivity);
                            RegInvoice m = new RegInvoice();
                            m.setStore_id(Integer.valueOf(store_id));
                            m.setMonto(montoFactura);
                            m.setRazonSocial(razonSocial);
                            m.setFile(thumbnail.getTag().toString());
                            mr.insert(m);

                            Intent intent = new Intent(MyActivity, UploadService.class);
                            //Log.i("FOO", uri.toString());
                            Bundle argPDV = new Bundle();

                            argPDV.putString("store_id",store_id.toString() );
                            argPDV.putString("product_id","0" );
                            argPDV.putString("poll_id",poll_id.toString() );
                            argPDV.putString("publicities_id","0" );
                            argPDV.putString("company_id",String.valueOf(GlobalConstant.company_id));
                            argPDV.putString("category_product_id",categoria_id.toString());
                            argPDV.putString("sod_ventana_id","0");
                            argPDV.putString("url_insert_image","" );
                            argPDV.putString("monto",montoFactura );
                            argPDV.putString("razon_social",razonSocial );
                            argPDV.putString("tipo","1");
                            intent.putStringArrayListExtra("names_file", names_file);
                            intent.putExtras(argPDV);
                            startService(intent);
                            finish();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //copyFile(getAlbumDir() + "/" + listFile[i].getName(), getAlbumDirBackup() + "/" + listFile[i].getName());


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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
            Bundle bundle = getIntent().getExtras();

            Intent i = new Intent( FacturaNueva.this , FacturaNueva.class);
            Bundle bolsa = new Bundle();

//            bolsa.putString("store_id",store_id );
//            bolsa.putString("product_id",product_id );
//            bolsa.putString("poll_id",poll_id );
//            bolsa.putString("publicities_id",publicities_id );
//            bolsa.putString("company_id",company_id);
//            bolsa.putString("category_product_id",category_product_id);
//            bolsa.putString("sod_ventana_id",sod_ventana_id);
//            bolsa.putString("monto",monto );
//            bolsa.putString("razon_social",razon_social );
//            bolsa.putString("url_insert_image",url_insert_image );
//            bolsa.putString("tipo",tipo);


            bolsa.putInt("store_id", Integer.valueOf(store_id));
            bolsa.putInt("rout_id", Integer.valueOf(rout_id));
            bolsa.putInt("categoria_id", Integer.valueOf(categoria_id));
            bolsa.putString("fechaRuta", fechaRuta);
            bolsa.putInt("audit_id", audit_id);
            bolsa.putString("montoCuota", montoCuota);


            i.putExtras(bolsa);
            startActivity(i);
            finish();
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }


    public void getFromSdcard()
    {
        //Bundle bundle = getIntent().getExtras();
       // String store_id = bundle.getString("store_id");

        File file= new File(BitmapLoader.getAlbumDir(MyActivity).getAbsolutePath());
        //File file= new File(Environment.getExternalStorageDirectory().toString()+ GlobalConstant.directory_images + BitmapLoader.getAlbumDir(MyActivity));

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            if (listFile != null){
                for (int i = 0; i < listFile.length; i++)
                {
                    if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", Integer.parseInt(store_id.toString())) ))
                    {
                       // f.add(listFile[i].getAbsolutePath());
                        if(listFile[i].exists()){
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            Bitmap myBitmap = BitmapFactory.decodeFile(listFile[i].getAbsolutePath(), options);

                            //thumbnail.setImageBitmap(myBitmap);
                            thumbnail.setImageBitmap(BitmapLoader.rotateImage(myBitmap,90));

                            thumbnail.setTag(listFile[i].getName().toString());
                        }
                    }

                }
            }


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


