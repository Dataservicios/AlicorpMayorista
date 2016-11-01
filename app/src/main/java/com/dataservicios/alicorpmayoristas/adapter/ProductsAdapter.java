package com.dataservicios.alicorpmayoristas.adapter;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dataservicios.alicorpmayoristas.Model.Product;

import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;
import com.dataservicios.alicorpmayoristas.app.AppController;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;
import com.dataservicios.alicorpmayoristas.util.SessionManager;

import java.util.List;



/**
 * Created by Jaime Eduardo on 30/09/2015.
 */
public class ProductsAdapter extends BaseAdapter {
    private static final String TAG = ProductsAdapter.class.getSimpleName();
    private Activity activity;
    private LayoutInflater inflater;
    private List<Product> productsItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductsAdapter(Activity activity, List<Product> productsItems) {
        this.activity = activity;
        this.productsItems = productsItems;

    }


    @Override
    public int getCount() {
        return productsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return productsItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        //View view = convertView;
        //LayoutInflater inflater = ((Activity) mycontext).getLayoutInflater();
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater = (activity).getLayoutInflater();
        if (convertView == null)


            convertView = inflater.inflate(R.layout.list_row_product, null);

        if (imageLoader == null)  imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);

        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);
        ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);
        //Switch swVisible = (Switch) convertView.findViewById(R.id.swEstaPro);




        Product m = productsItems.get(position);
        thumbNail.setImageUrl(GlobalConstant.dominio + "/media/images/alicorp/publicities/" + m.getImage(), imageLoader);
        tvId.setText(String.valueOf(m.getId()));
        tvName.setText(m.getName());
        tvCategoryName.setText( m.getCategory_name());
        if(m.getActive()==0){
            imgStatus.setImageResource(R.drawable.ic_check_on);
        } else if(m.getActive()==1){
            imgStatus.setImageResource(R.drawable.ic_check_off);
        }



        return convertView;
    }


    @Override
    public boolean isEnabled(int position) {

        // Deshabilitando los items del adptador segun el statu
        if( productsItems.get(position).getActive()==0){

            return false;

        }
        return true;
    }

}
