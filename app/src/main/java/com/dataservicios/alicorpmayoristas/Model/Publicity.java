package com.dataservicios.alicorpmayoristas.Model;

/**
 * Created by Jaime Eduardo on 06/10/2015.
 */
public class Publicity {

    private int id;
    private String name;
    private String image;
    private int category_id;
    private String category_name;
    private int company_id;
    private int active;


    /**
     * Get id
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Set id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get Name Publicity
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *  Set Name Publicity
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Image Publicity
     * @return
     */
    public String getImage() {
        return image;
    }

    /**
     * Set Image Publicity
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Get Category_id Publicity
     * @return
     */
    public int getCategory_id() {
        return category_id;
    }

    /**
     * Set Category_id Publicity
     * @param category_id
     */
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    /**
     * Get Category_name Publicity
     * @return
     */
    public String getCategory_name() {
        return category_name;
    }

    /**
     * Set Category_name Publicity
     * @param category_name
     */
    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    /**
     * Get Company_id Publicity
     * @return
     */
    public int getCompany_id() {
        return company_id;
    }

    /**
     * Set Company_id Publicity
     * @param company_id
     */
    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    /**
     * Get Setatus Publicity
     * @return
     */
    public int getActive() {
        return active;
    }

    /**
     * Set estatus publicity
     * @param active
     */
    public void setActive(int active) {
        this.active = active;
    }
}
