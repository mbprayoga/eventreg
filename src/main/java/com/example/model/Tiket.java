// Tiket.java
package com.example.model;

public class Tiket {
    // Properties for the tiket table
    private int id;
    private int idPengguna;
    private int idEvent;
    private java.sql.Date tanggal;

    // Getter and Setter Methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public java.sql.Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(java.sql.Date tanggal) {
        this.tanggal = tanggal;
    }
}