package com.kecipir.kecipir.data;

/**
 * Created by Kecipir-Dev on 29/12/2016.
 */

public class DataMember {

    private String nama;
    private String email;
    private String noTelp;
    private String alamat;
    private String tglDaftar;
    private String jmlBelanja;

    public DataMember() {
    }

    public DataMember(String nama, String email, String noTelp, String alamat, String tglDaftar, String jmlBelanja) {
        this.nama = nama;
        this.email = email;
        this.noTelp = noTelp;
        this.alamat = alamat;
        this.tglDaftar = tglDaftar;
        this.jmlBelanja = jmlBelanja;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTglDaftar() {
        return tglDaftar;
    }

    public void setTglDaftar(String tglDaftar) {
        this.tglDaftar = tglDaftar;
    }

    public String getJmlBelanja() {
        return jmlBelanja;
    }

    public void setJmlBelanja(String jmlBelanja) {
        this.jmlBelanja = jmlBelanja;
    }
}
