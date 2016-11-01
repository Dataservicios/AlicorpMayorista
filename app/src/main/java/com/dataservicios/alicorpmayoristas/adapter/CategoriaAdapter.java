package com.dataservicios.alicorpmayoristas.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.dataservicios.alicorpmayoristas.Model.Categoria;
import com.dataservicios.alicorpmayoristas.Model.Product;
import com.dataservicios.alicorpmayoristas.R;
import com.dataservicios.alicorpmayoristas.util.GlobalConstant;


/**
 * Created by usuario on 30/01/2015.
 */
public class CategoriaAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Categoria> categoriaItems;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CategoriaAdapter(Activity activity, List<Categoria> categoriaItems) {
        this.activity = activity;
        this.categoriaItems = categoriaItems;
    }

    @Override
    public int getCount() {
        return categoriaItems.size();
    }

    @Override
    public Object getItem(int location) {
        return categoriaItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_categorias, null);
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        //TextView nombre = (TextView) convertView.findViewById(R.id.tvNombre);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);

        // getting ruta data for the row
        Categoria m = categoriaItems.get(position);


        tvId.setText(String.valueOf(m.getId()));
        tvName.setText(m.getNombre());
        if(m.getActive()==0){
            imgStatus.setImageResource(R.drawable.ic_check_on);
        } else if(m.getActive()==1){
            imgStatus.setImageResource(R.drawable.ic_check_off);
        }

        return convertView;
    }

}