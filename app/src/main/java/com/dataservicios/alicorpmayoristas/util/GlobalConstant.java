package com.dataservicios.alicorpmayoristas.util;
/**
 * Created by usuario on 11/11/2014.
 */
public final class GlobalConstant {
    public static String dominio = "http://ttaudit.com";
    //public static String dominio = "http://appfiliaibk.com";
    public static final String LOGIN_URL = dominio + "/loginUser";
    public static final String KEY_USERNAME = "username";
    public static String inicio,fin;
    public static  double latitude_open, longitude_open;
    public static  int global_close_audit =0;
    public static int company_id = 42;
   // public static String albunName = "AlicorpPhoto";
    //public static String directory_images = "/Pictures/" + albunName;
    public static String directory_images = "/Pictures/" ;
    public static String type_aplication = "android";
    public static int[] poll_id = new int[]{

            610 ,	// 0 "Existe Ventana?"
            611 ,	// 1 "Cumple Visibilidad?"
            612 ,	// 2 "Encontro producto?"
            613 ,	// 3 "Cliente cumplio cuota?"
            614 ,	// 4 "Cliente acepto dar facturas?"
            615 ,	// 5 "Se encuentra Abierto el punto?"
            616 ,	// 6 "¿Cliente permitió tomar información?"
    } ;

    public static int[] audit_id = new int[]{
            39,	// 0 "Visibilidad Alicorp Mayoristas"
            40,	// 1 "Cumple MSL Alicorp Mayorista"
            41,	// 2 "Cumple Montos Alicorp Mayorista"
            42,	// 3 "Preguntas iniciales Alicorp Mayorista"
    } ;
}

