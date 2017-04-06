package com.kecipir.kecipir.data;

/**
 * Created by Albani on 9/17/2015.
 */
public class HistoryDetail {

    private String foto;
    private String namaBarang;
    private String petani;
    private String grade;
    private String satuan;
    private String quantity;
    private String harga;
    private String total;

    public HistoryDetail(){

    }

    public HistoryDetail(String foto, String namaBarang, String petani, String grade, String satuan, String quantity, String harga, String total){
        this.foto = foto;
        this.namaBarang = namaBarang;
        this.petani = petani;
        this.grade = grade;
        this.satuan = satuan;
        this.quantity = quantity;
        this.harga = harga;
        this.total = total;

    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getPetani() {
        return petani;
    }

    public void setPetani(String petani) {
        this.petani = petani;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
