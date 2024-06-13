package com.example.model;

public class Event {
    // Properties sesuai dengan tabel Event
    private int id;
    private String nama;
    private java.sql.Date tanggal;
    private String lokasi;
    private String deskripsi;
    private double biaya;
    private int idPenyelenggara;

    // Getter and Setter Methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public java.sql.Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(java.sql.Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getBiaya() {
        return biaya;
    }

    public void setBiaya(double biaya) {
        this.biaya = biaya;
    }

    public int getIdPenyelenggara() {
        return idPenyelenggara;
    }

    public void setIdPenyelenggara(int idPenyelenggara) {
        this.idPenyelenggara = idPenyelenggara;
    }
}