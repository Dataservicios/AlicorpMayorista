package com.dataservicios.alicorpmayoristas.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataservicios.alicorpmayoristas.AlbumStorageDirFactory;
import com.dataservicios.alicorpmayoristas.BaseAlbumDirFactory;
import com.dataservicios.alicorpmayoristas.FroyoAlbumDirFactory;
import com.dataservicios.alicorpmayoristas.Model.Media;
import com.dataservicios.alicorpmayoristas.Model.RegInvoice;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.util.BitmapLoader;

import java.io.File;
import java.util.List;

/**
 * Created by Jaime on 28/11/2016.
 */

public class RegFacturaAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<RegInvoice> regInvoiceItems;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    public RegFacturaAdapter(Activity activity, List<RegInvoice> regInvoiceItems) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        this.activity = activity;
        this.regInvoiceItems = regInvoiceItems;
    }




    @Override
    public int getCount() {
        return regInvoiceItems.size();
    }

    @Override
    public Object getItem(int position) {
        return regInvoiceItems.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View view = convertView;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_reg_factura, null);

        // if (imageLoader == null)  imageLoader = AppController.getInstance().getImageLoader();

        //NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail) ;
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvStoreId = (TextView) convertView.findViewById(R.id.tvStoreId);
        TextView tvRazonSocial = (TextView) convertView.findViewById(R.id.tvRazonSocial);
        TextView tvMontoFactura = (TextView) convertView.findViewById(R.id.tvMontoFactura);

        //ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);

        RegInvoice m = regInvoiceItems.get(position);

        //thumbNail.setImageUrl(m.getImage(), imageLoader);
        String pathFile = BitmapLoader.getAlbumDirBackup(activity).getAbsolutePath() + "/" + m.getFile() ;
        File imgFile = new File(pathFile);


        if(imgFile.exists()){

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            //thumbNail.setImageBitmap(myBitmap);
            thumbNail.setImageBitmap(BitmapLoader.rotateImage(myBitmap,90));

        }


        tvId.setText(String.valueOf(m.getId()));
        tvStoreId.setText(String.valueOf(m.getStore_id()));
        tvRazonSocial.setText(m.getRazonSocial().toString());
        tvMontoFactura.setText(m.getMonto().toString());

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {

        // Deshabilitando los items del adptador segun el statu
//        if( mediaItems.get(position).getActive()==1){
//
//            return false;
//
//        }
        return true;
    }



}
