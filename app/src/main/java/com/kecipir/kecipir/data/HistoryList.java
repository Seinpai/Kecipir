package com.kecipir.kecipir.data;

/**
 * Created by Albani on 11/20/2015.
 */
public class HistoryList {
    private String nota;
    private String qty;
    private String totalPembelian;
    private String tanggal;
    private String tgl_panen;
    private String namaMember;
    private String stsBayar;

    public HistoryList(){

    }

    public HistoryList(String nota, String qty, String totalPembelian, String tanggal, String tgl_panen, String namaMember, String stsBayar){
        this.nota = nota;
        this.qty = qty;
        this.totalPembelian = totalPembelian;
        this.tanggal = tanggal;
        this.tgl_panen = tgl_panen;
        this.namaMember = namaMember;
        this.stsBayar = stsBayar;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotalPembelian() {
        return totalPembelian;
    }

    public void setTotalPembelian(String totalPembelian) {
        this.totalPembelian = totalPembelian;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTgl_panen() {
        return tgl_panen;
    }

    public void setTgl_panen(String tgl_panen) {
        this.tgl_panen = tgl_panen;
    }

    public String getNamaMember() {
        return namaMember;
    }

    public void setNamaMember(String namaMember) {
        this.namaMember = namaMember;
    }

    public String getStsBayar() {
        return stsBayar;
    }

    public void setStsBayar(String stsBayar) {
        this.stsBayar = stsBayar;
    }
}

