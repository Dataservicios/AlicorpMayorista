package com.dataservicios.alicorpmayoristas.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import  com.dataservicios.alicorpmayoristas.Model.Publicity;
import  com.dataservicios.alicorpmayoristas.R;
import  com.dataservicios.alicorpmayoristas.app.AppController;
import  com.dataservicios.alicorpmayoristas.util.GlobalConstant;

/**
 * Created by Jaime Eduardo on 06/10/2015.
 */
public class PublicityAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Publicity> publicityItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PublicityAdapter(Activity activity, List<Publicity> publicityItems) {
        this.activity = activity;
        this.publicityItems = publicityItems;
    }

    @Override
    public int getCount() {
        return publicityItems.size();
    }

    @Override
    public Object getItem(int position) {
        return publicityItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View view = convertView;
        //LayoutInflater inflater = ((Activity) mycontext).getLayoutInflater();
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater = (activity).getLayoutInflater();
        if (convertView == null)


            convertView = inflater.inflate(R.layout.list_row_publicity, null);

                         if (imageLoader == null)  imageLoader = AppController.getInstance().getImageLoader();

                         NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);

                        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
                        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
                        TextView tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);
                        ImageView imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);
                        //Switch swVisible = (Switch) convertView.findViewById(R.id.swEstaPro);

//                        swVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                if (isChecked) {
//                                    // do something when check is selected
//                                    Toast toast = Toast.makeText(activity , "Marco Si  "    , Toast.LENGTH_SHORT );
//                                    toast.show();
//
//
//                                } else {
//                                    //do something when unchecked
//                                    // do something when check is selected
//                                    Toast toast = Toast.makeText(activity , "Marco No  "    , Toast.LENGTH_SHORT );
//                                    toast.show();
//                                }
//                            }
//                        });


                        Publicity m = publicityItems.get(position);
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
        if( publicityItems.get(position).getActive()==0){

            return false;

        }
        return true;
    }

}
