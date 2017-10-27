package com.kecipir.kecipir.data;

/**
 * Created by Kecipir-Dev on 09/12/2016.
 */

public class CodMemberList {

    private String no_nota;
    private String nama;
    private String tanggal;
    private String id_host;
    private String total;
    private String ket;

    public CodMemberList() {
    }

    public CodMemberList(String no_nota, String nama, String tanggal, String id_host, String total, String ket) {
        this.no_nota = no_nota;
        this.nama = nama;
        this.tanggal = tanggal;
        this.id_host = id_host;
        this.total = total;
        this.ket = ket;
    }

    public String getNo_nota() {
        return no_nota;
    }

    public void setNo_nota(String no_nota) {
        this.no_nota = no_nota;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getId_host() {
        return id_host;
    }

    public void setId_host(String id_host) {
        this.id_host = id_host;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }
}
