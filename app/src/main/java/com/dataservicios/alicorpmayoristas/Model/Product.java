package com.dataservicios.alicorpmayoristas.Model;

/**
 * Created by Jaime Eduardo on 29/09/2015.
 */
public class Product {

    private int id;

    private String name;
    private String code;
    private String image;
    private int category_id;
    private String category_name;
    private int company_id;
    private String precio;
    private int status;
    private int active;
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }







    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }




    public Product() {

    }

    public Product(int id, String name, String code, String image , int company_id , int category_id, String category_name) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.image = image;
        this.setCompany_id(company_id);
    }

    /**
     *
     * @return id
     */

    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    /**
     *
     * @return category_id
     */
    public int getCategory_id() {
        return category_id;
    }

    /**
     *
     * @param category_id
     */
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    /**
     *
     * @return category_name
     */
    public String getCategory_name() {
        return category_name;
    }

    /**
     *
     * @param category_name
     */
    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
