package com.dataservicios.alicorpmayoristas.Model;

/**
 * Created by Jaime on 27/10/2016.
 */

public class Cuota {
    private int store_id;
    private String cuota;

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getCuota() {
        return cuota;
    }

    public void setCuota(String cuota) {
        this.cuota = cuota;
    }
}
