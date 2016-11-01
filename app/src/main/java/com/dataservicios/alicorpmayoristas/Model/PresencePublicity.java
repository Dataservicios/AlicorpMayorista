package com.dataservicios.alicorpmayoristas.Model;

/**
 * Created by Jaime Eduardo on 08/10/2015.
 */
public class PresencePublicity {
    private  int id;
    private int store_id;
    private int category_id;
    private int publicity_id;
    private int found;
    private int visible;
    private int layout_correcto;
    private int contaminated;
    private String comment;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getPublicity_id() {
        return publicity_id;
    }

    public void setPublicity_id(int publicity_id) {
        this.publicity_id = publicity_id;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getLayout_correcto() {
        return layout_correcto;
    }

    public void setLayout_correcto(int layout_correcto) {
        this.layout_correcto = layout_correcto;
    }

    public int getContaminated() {
        return contaminated;
    }

    public void setContaminated(int contaminated) {
        this.contaminated = contaminated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
