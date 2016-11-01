package com.dataservicios.alicorpmayoristas.Model;

/**
 * Created by usuario on 23/03/2015.
 */
public class User {

    private int  id;
    private String name, email, password;

    public User() {
    }

    /**
     *
     * @param id
     * @param name
     * @param email
     * @param password
     */
    public User(int id, String name, String email, String password) {
        this.id= id;
        this.name = name;
        this.password = password;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param Id
     */
    public void setId(int Id) {
        this.id = id;
    }

    public String getName() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
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
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
