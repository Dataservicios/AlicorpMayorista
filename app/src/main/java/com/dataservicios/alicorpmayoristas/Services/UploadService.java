package com.dataservicios.alicorpmayoristas.Services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.dataservicios.alicorpmayoristas.AlbumStorageDirFactory;
import com.dataservicios.alicorpmayoristas.Model.Media;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.Repositories.MediaRepo;
import com.dataservicios.alicorpmayoristas.util.BitmapLoader;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;

//Subida de Archivos


public class  UploadService extends IntentService {
    private static final String LOG_TAG = UploadService.class.getSimpleName();
    public static final int NOTIFICATION_ID=1;
    private int totalMessages = 0;
    private NotificationManager mNotificationManager;
    private Notification notification;
    private Context context = this;

    ArrayList<String> names_file = new ArrayList<String>();
    private static final String url_upload_image = GlobalConstant.dominio + "/uploadImagesAudit";
    private String url_insert_image ;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    String store_id,publicities_id,invoices_id,tipo,product_id,sod_ventana_id, company_id, poll_id,category_product_id;

    public UploadService(String name) {
        super(name);
    }
    public UploadService(){
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        names_file =intent.getStringArrayListExtra("names_file");
        store_id=intent.getStringExtra("store_id");

        publicities_id=intent.getStringExtra("publicities_id");
        product_id=intent.getStringExtra("product_id");
        poll_id=intent.getStringExtra("poll_id");
        company_id = intent.getStringExtra("company_id");
        category_product_id =intent.getStringExtra("category_product_id");
        sod_ventana_id = intent.getStringExtra("sod_ventana_id");
        url_insert_image=intent.getStringExtra("url_insert_image");
        tipo=intent.getStringExtra("tipo");

        for (int i = 0; i < names_file.size(); i++) {
            String foto = names_file.get(i);
            //String pathFile =getAlbumDirTemp().getAbsolutePath() + "/" + foto ;
            String created_at = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
            MediaRepo mr = new MediaRepo(context);
            Media m = new Media();
            m.setStore_id(Integer.valueOf(store_id));
            m.setPoll_id(Integer.valueOf(poll_id));
            m.setCompany_id(GlobalConstant.company_id);
            m.setCategory_product_id(Integer.valueOf(category_product_id));
            m.setPublicity_id(Integer.valueOf(publicities_id));
            m.setCreated_at(created_at);
            m.setType(1);
            m.setFile(foto);
            mr.insert(m);
        }
    }



}
