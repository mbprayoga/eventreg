package com.example.model;

public class EventSeminar extends Event{
    private String pembicara;
    private String tema;

    public String getPembicara() {
        return pembicara;
    }

    public void setPembicara(String pembicara) {
        this.pembicara = pembicara;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }
}
