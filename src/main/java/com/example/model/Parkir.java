// Parkir.java
package com.example.model;

public class Parkir {
    // Properties for the parkir table
    private int id;
    private double biaya;
    private String noParkir;
    private int idTiket;

    // Getter and Setter Methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBiaya() {
        return biaya;
    }

    public void setBiaya(double biaya) {
        this.biaya = biaya;
    }

    public String getNoParkir() {
        return noParkir;
    }

    public void setNoParkir(String noParkir) {
        this.noParkir = noParkir;
    }

    public int getIdTiket() {
        return idTiket;
    }

    public void setIdTiket(int idTiket) {
        this.idTiket = idTiket;
    }
}